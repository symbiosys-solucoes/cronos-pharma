package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.controllers

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.request.Accounts
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.response.UpsertResponse
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.services.AccountsService
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.services.AuthorizationTokenService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.beans.factory.annotation.Autowired

import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/petronas/Sfa/BR/Cust")
class SFACustomersController {

    @Autowired
    lateinit var accountsService: AccountsService

    @Autowired
    lateinit var authService: AuthorizationTokenService
    @PostMapping
    fun createAccounts( @RequestBody request: List<Accounts>, @RequestHeader(HttpHeaders.AUTHORIZATION) token: String ): List<UpsertResponse> {
        authService.validate(token)
      return listOf()//accountsService.createAccounts(request)
    }


}