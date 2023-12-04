package br.symbiosys.solucoes.cronospharma.modules.petronas.ports.controllers

import br.symbiosys.solucoes.cronospharma.commons.ROTAS
import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.Accounts
import br.symbiosys.solucoes.cronospharma.modules.petronas.usecases.SendAccountsToSFAUseCase
import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.data.repository.query.Param
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(ROTAS.PETRONAS_ENVIO_CLIENTES)
class AccountsController (private val sendAccountsToSFAUseCase: SendAccountsToSFAUseCase) {


    @PostMapping
    suspend fun send(): AccountsResponse {
        try {
            sendAccountsToSFAUseCase.executeAsync()
            return AccountsResponse("Enviando Carga de Clientes para a Petronas")
        } catch (e: Exception) {
            throw InternalError(e.message)
        }

    }

    @PostMapping("/{code}")
    fun send(@PathVariable("code") customerCode: String): ResponseEntity<AccountsResponse> {
        return try {
            val account: Accounts = sendAccountsToSFAUseCase.sendAccount(customerCode)
            ResponseEntity.ok(AccountsResponse("Carga de Clientes para a Petronas enviada com sucesso", account))
        } catch (e: Exception) {
            ResponseEntity.status(500).body(e.message?.let { AccountsResponse(it) })
        }

    }
}

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AccountsResponse(val message: String, val data: Accounts? = null)
