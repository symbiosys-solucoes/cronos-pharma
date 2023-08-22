package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.services

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos.CliForRepository
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.api.ApiPetronasUpsertAccounts
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.request.Accounts
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.response.UpsertResponse
import br.symbiosys.solucoes.cronospharma.cronospharma.sym.gateway.repository.SymCustomerRepository
import br.symbiosys.solucoes.cronospharma.cronospharma.sym.model.SymCustomer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class AccountsService {

    @Autowired
    private lateinit var symCustomerRepository: SymCustomerRepository

    @Autowired
    private lateinit var cliForRepository: CliForRepository

    @Autowired
    private lateinit var apiPetronasUpsertAccounts: ApiPetronasUpsertAccounts

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

    fun sendAccountsToSfa(codigoDistribuidor: String) {
        val clientesCronos = cliForRepository.findAll()
        val clientesSales = symCustomerRepository.findAll().map { it.codigoCronos }
        val clientesNovosCronos = clientesCronos.filter { !clientesSales.contains(it.codCliFor) }

        val accounts = clientesNovosCronos.map { Accounts.fromCliFor(it, codigoDistribuidor) }.chunked(50).toList()
        for (request in accounts) {
            val response = apiPetronasUpsertAccounts.upsertAccounts(request)
            if (response.statusCode == HttpStatus.OK) {
                val body = response.body!!
                body.filter { it.isSuccess && it.isCreated }.forEach {
                    val customer = SymCustomer().apply {
                        idIntegrador = it.sfdcId
                        codigoCronos = it.externalId?.replace("$codigoDistribuidor-", "")
                        codigoIntegrador = it.externalId
                        tipoIntegrador = "PETRONAS"
                    }
                    symCustomerRepository.save(customer)
                }

            }
        }
    }
}
