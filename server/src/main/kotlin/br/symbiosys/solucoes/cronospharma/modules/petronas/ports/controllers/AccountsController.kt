package br.symbiosys.solucoes.cronospharma.modules.petronas.ports.controllers

import br.symbiosys.solucoes.cronospharma.commons.ROTAS
import br.symbiosys.solucoes.cronospharma.modules.petronas.usecases.SendAccountsToSFAUseCase
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
}

data class AccountsResponse(val message: String)