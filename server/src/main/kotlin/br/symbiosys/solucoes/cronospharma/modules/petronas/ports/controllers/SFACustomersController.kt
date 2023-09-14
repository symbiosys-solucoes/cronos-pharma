package br.symbiosys.solucoes.cronospharma.modules.petronas.ports.controllers

import br.symbiosys.solucoes.cronospharma.commons.AuthorizationTokenService
import br.symbiosys.solucoes.cronospharma.commons.ROTAS
import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.Accounts
import br.symbiosys.solucoes.cronospharma.modules.petronas.models.response.UpsertResponse
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(ROTAS.PETRONAS_CADASTRAR_CLIENTES)
class SFACustomersController (private val authService: AuthorizationTokenService){

    @PostMapping
    fun createAccounts(
        @RequestBody request: List<Accounts>,
        @RequestHeader(HttpHeaders.AUTHORIZATION) token: String
    ): List<UpsertResponse> {
        authService.validate(token)
        return listOf()
    }


}