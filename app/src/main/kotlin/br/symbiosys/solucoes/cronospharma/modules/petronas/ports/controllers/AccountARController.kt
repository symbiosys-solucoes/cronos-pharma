package br.symbiosys.solucoes.cronospharma.modules.petronas.ports.controllers

import br.symbiosys.solucoes.cronospharma.commons.ROTAS
import br.symbiosys.solucoes.cronospharma.modules.petronas.usecases.SendAccountARsToSFAUseCase
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class AccountARController (private val sendAccountARsToSFAUseCase: SendAccountARsToSFAUseCase) {


    @PostMapping(ROTAS.PETRONAS_ENVIO_CPR)
    suspend fun send(): AccountARResponse {
        try {
            sendAccountARsToSFAUseCase.executeAsync()
            return AccountARResponse("Enviando carga de Titulos para PETRONAS")
        } catch (e: Exception) {
            throw InternalError(e.message)
        }

    }


}

data class AccountARResponse(val message: String)