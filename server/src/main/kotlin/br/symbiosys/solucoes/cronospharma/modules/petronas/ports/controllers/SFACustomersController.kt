package br.symbiosys.solucoes.cronospharma.modules.petronas.ports.controllers

import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.Accounts
import br.symbiosys.solucoes.cronospharma.modules.petronas.models.response.UpsertResponse
import br.symbiosys.solucoes.cronospharma.commons.AuthorizationTokenService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/petronas/Sfa/BR/Cust")
class SFACustomersController {


    @Autowired
    lateinit var authService: AuthorizationTokenService

    @PostMapping
    fun createAccounts(
        @RequestBody request: List<Accounts>,
        @RequestHeader(HttpHeaders.AUTHORIZATION) token: String
    ): List<UpsertResponse> {
        authService.validate(token)
        return listOf()
    }


}