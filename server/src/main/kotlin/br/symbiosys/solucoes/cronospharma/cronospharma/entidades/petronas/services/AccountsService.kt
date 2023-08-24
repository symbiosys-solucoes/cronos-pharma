package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.services

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos.CliForRepository
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.api.ApiPetronasUpsertAccounts
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.request.Accounts
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.response.UpsertResponse
import br.symbiosys.solucoes.cronospharma.cronospharma.sym.gateway.repository.SymCustomerRepository
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
    private lateinit var symCustomerRepository: SymCustomerRepository

    @Autowired
    private lateinit var symErrosRepository: SymErrosRepository

    @Autowired
    private lateinit var cliForRepository: CliForRepository

    @Autowired
    private lateinit var apiPetronasUpsertAccounts: ApiPetronasUpsertAccounts

    val logger = LoggerFactory.getLogger(AccountsService::class.java)
    val mapper = ObjectMapper()

    fun createAccounts(request: List<Accounts>): List<UpsertResponse> {

        val response = mutableListOf<UpsertResponse>()
        request.forEach { account ->
            run {
                val exists = symCustomerRepository.findBycpfCnpjAndTipoIntegrador(account.cnpj?:"", account.accountSource?:"SFA")
                if (exists.isEmpty()) {
                    val symCustomer = account.toSymCustomer()
                    val customer = symCustomerRepository.save(symCustomer)
                    val clienteCronos = cliForRepository.createFromSymCustomer(customer.id!!)
                    customer.codigoCronos = clienteCronos?.codCliFor
                    response.add(UpsertResponse().apply {
                        isSuccess = true
                        sfdcId = customer.idIntegrador ?: ""
                        isCreated = true
                        externalId =  "${account.dtCode}-${customer.codigoCronos}"
                        errors = ""
                    })
                } else {
                    response.add(UpsertResponse().apply {
                        isSuccess = false
                        sfdcId = account.salesForceId?: ""
                        isCreated = true
                        externalId =  "${account.dtCode}-${exists.get().codigoCronos}"
                        errors = "cliente ja cadastrado na base de dados"
                    })
                }

            }
        }

        return response
    }

    fun sendAccountsToSfa(symParametros: SymParametros) {
        val clientesCronos = cliForRepository.findAll(symParametros)
        println(clientesCronos.size)
        val clientesSales = symCustomerRepository.findAll().map { it.codigoCronos }
        val clientesNovosCronos = clientesCronos.filter { !clientesSales.contains(it.codCliFor) }

        val accounts = clientesNovosCronos.map { Accounts.fromCliFor(it, symParametros.codigoDistribuidorPetronas!!) }.chunked(50).toList()
        var i = 1
        val erros = mutableListOf<SymErros>()
        for (request in accounts) {
            logger.info("enviando request {$i} de ${accounts.size} para SFA")
            i++
            val response = apiPetronasUpsertAccounts.upsertAccounts(request)
            if (response.statusCode == HttpStatus.OK) {
                val body = response.body!!
                body.filter { it.isSuccess && it.isCreated }.forEach {
                    val customer = SymCustomer().apply {
                        idIntegrador = it.sfdcId
                        codigoCronos = it.externalId?.replace("${symParametros.codigoDistribuidorPetronas}-", "")
                        codigoIntegrador = it.externalId
                        tipoIntegrador = "PETRONAS"
                    }
                    symCustomerRepository.save(customer)
                }

                body.filter { !it.isSuccess }.forEach {
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
