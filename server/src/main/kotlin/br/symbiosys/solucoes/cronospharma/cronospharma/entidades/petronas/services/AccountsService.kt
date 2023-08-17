package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.services

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos.CliForRepository
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.request.Accounts
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.response.UpsertResponse
import br.symbiosys.solucoes.cronospharma.cronospharma.sym.gateway.repository.SymCustomerRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AccountsService {

    @Autowired
    lateinit var symCustomerRepository: SymCustomerRepository

    @Autowired
    lateinit var cliForRepository: CliForRepository

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
}
