package br.symbiosys.solucoes.cronospharma.modules.petronas.ports.controllers

import br.symbiosys.solucoes.cronospharma.modules.petronas.usecases.SendAccountARsToSFAUseCase
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/petronas/envio/cpr")
class AccountARController (private val sendAccountARsToSFAUseCase: SendAccountARsToSFAUseCase) {


    @PostMapping
    fun send() {
        try {
            sendAccountARsToSFAUseCase.execute()
        } catch (e: Exception) {
            throw InternalError(e.message)
        }

    }
}