package br.symbiosys.solucoes.cronospharma.modules.petronas.ports.controllers

import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.Order
import br.symbiosys.solucoes.cronospharma.modules.petronas.models.response.UpsertResponse
import br.symbiosys.solucoes.cronospharma.commons.AuthorizationTokenService
import br.symbiosys.solucoes.cronospharma.commons.ROTAS
import br.symbiosys.solucoes.cronospharma.modules.petronas.usecases.SendOrderToERPUseCase
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(ROTAS.PETRONAS_CADASTRAR_PEDIDOS)
class SFAOrderController
    (private val sendOrderToERPUseCase: SendOrderToERPUseCase, private val authService: AuthorizationTokenService) {

    @PostMapping
    fun createOrder(
        @RequestBody request: List<Order>,
        @RequestHeader(HttpHeaders.AUTHORIZATION) token: String
    ): List<UpsertResponse> {
        authService.validate(token)
        return sendOrderToERPUseCase.execute(request)
    }

}
