package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.controllers

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.services.AccountsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/petronas/envio/clientes")
class AccountsController {

    @Autowired
    lateinit var accountsService: AccountsService

    @PostMapping
    fun send() {
        try {
            accountsService.sendAccountsToSfa()
        } catch (e: Exception) {
            throw InternalError(e.message)
        }

    }
}