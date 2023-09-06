package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.services

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos.CliForRepository
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.api.ApiPetronasUpsertAccounts
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.request.Accounts
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.response.UpsertResponse
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.repositories.PetronasAccountsRepository
import br.symbiosys.solucoes.cronospharma.cronospharma.sym.gateway.repository.SymErrosRepository
import br.symbiosys.solucoes.cronospharma.cronospharma.sym.model.SymCustomer
import br.symbiosys.solucoes.cronospharma.cronospharma.sym.model.SymErros
import br.symbiosys.solucoes.cronospharma.cronospharma.sym.model.SymParametros
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class AccountsService {

    @Autowired
    private lateinit var symErrosRepository: SymErrosRepository

    @Autowired
    private lateinit var petronasAccountsRepository: PetronasAccountsRepository

    @Autowired
    private lateinit var apiPetronasUpsertAccounts: ApiPetronasUpsertAccounts

    val logger = LoggerFactory.getLogger(AccountsService::class.java)
    val mapper = ObjectMapper()

//    fun createAccounts(request: List<Accounts>): List<UpsertResponse> {
//
//        val response = mutableListOf<UpsertResponse>()
//        request.forEach { account ->
//            run {
//                val exists = symCustomerRepository.findBycpfCnpjAndTipoIntegrador(account.cnpj?:"", account.accountSource?:"SFA")
//                if (exists.isEmpty()) {
//                    val symCustomer = account.toSymCustomer()
//                    val customer = symCustomerRepository.save(symCustomer)
//                    val clienteCronos = cliForRepository.createFromSymCustomer(customer.id!!)
//                    customer.codigoCronos = clienteCronos?.codCliFor
//                    response.add(UpsertResponse().apply {
//                        isSuccess = true
//                        sfdcId = customer.idIntegrador ?: ""
//                        isCreated = true
//                        externalId =  "${account.dtCode}-${customer.codigoCronos}"
//                        errors = ""
//                    })
//                } else {
//                    response.add(UpsertResponse().apply {
//                        isSuccess = false
//                        sfdcId = account.salesForceId?: ""
//                        isCreated = true
//                        externalId =  "${account.dtCode}-${exists.get().codigoCronos}"
//                        errors = "cliente ja cadastrado na base de dados"
//                    })
//                }
//
//            }
//        }
//
//        return response
//    }

    fun sendAccountsToSfa() {
        val customers = petronasAccountsRepository.findAll()
        logger.info("Foram encontrados ${customers.size} clientes")

        var i = 1
        val erros = mutableListOf<SymErros>()
        val accounts = customers.chunked(50).toList()
        for (request in accounts) {
            logger.info("enviando request {$i} de ${accounts.size} para SFA")
            i++
            val response = apiPetronasUpsertAccounts.upsertAccounts(request)
            if (response.statusCode == HttpStatus.OK) {
                val body = response.body!!
                body.filter { it.isSuccess && it.isCreated }.forEach {

                    val accountNumber = it.externalId!!.split("-")[1]
                    logger.info("cliente ${it.externalId} cadastrado com sucesso")
                    petronasAccountsRepository.markAsCreated(it.sfdcId!!, accountNumber)

                }
                body.filter { !it.isCreated  && it.isSuccess }.forEach {
                    val accountNumber = it.externalId!!.split("-")[1]
                    logger.info("cliente ${it.externalId} atualizado com sucesso")
                    petronasAccountsRepository.markAsUpdated(it.sfdcId!!, accountNumber)
                }
                body.filter { !it.isSuccess }.forEach {
                    logger.error("cliente ${it.externalId} não foi atualizado nem cadastrado")
                    erros.add(SymErros().apply {
                        dataOperacao = LocalDateTime.now()
                        tipoOperacao = "CADASTRO CLIENTE SFA"
                        petronasResponse = mapper.writeValueAsString(it)
                    })
                }
            }
        }

        symErrosRepository.saveAll(erros)
    }
}
