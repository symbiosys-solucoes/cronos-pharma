package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.controllers

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.request.Accounts
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.response.UpsertResponse
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.services.AccountsService
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.services.AuthorizationTokenService
import org.springframework.beans.factory.annotation.Autowired

import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/petronas/Sfa/BR/Cust")
class SFACustomers {

    lateinit var accountsService: AccountsService

    @Autowired
    lateinit var authService: AuthorizationTokenService
    @PostMapping
    fun createAccounts( @RequestBody request: List<Accounts>, @RequestHeader(HttpHeaders.AUTHORIZATION) token: String ) {
        authService.validate(token)
        val accounts = accountsService.createAccounts(request)

      //  return accounts.map { account -> UpsertResponse().apply { isSuccess } }
    }

}