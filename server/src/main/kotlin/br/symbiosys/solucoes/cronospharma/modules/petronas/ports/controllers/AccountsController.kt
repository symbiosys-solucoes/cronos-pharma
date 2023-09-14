package br.symbiosys.solucoes.cronospharma.modules.petronas.ports.controllers

import br.symbiosys.solucoes.cronospharma.modules.petronas.usecases.SendAccountsToSFAUseCase
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/petronas/envio/clientes")
class AccountsController (private val sendAccountsToSFAUseCase: SendAccountsToSFAUseCase) {


    @PostMapping
    fun send() {
        try {
            sendAccountsToSFAUseCase.execute()
        } catch (e: Exception) {
            throw InternalError(e.message)
        }

    }
}