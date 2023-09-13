package br.symbiosys.solucoes.cronospharma.modules.petronas.ports.controllers

import br.symbiosys.solucoes.cronospharma.commons.AuthorizationTokenService
import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.OrderItem
import br.symbiosys.solucoes.cronospharma.modules.petronas.models.response.UpsertResponse
import br.symbiosys.solucoes.cronospharma.modules.petronas.usecases.SendOrderItemToERPUseCase
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/petronas/Sfa/BR/OrderItem")
class SFAOrderItemController
    (
    private val sendOrderItemToERPUseCase: SendOrderItemToERPUseCase,
    private val authService: AuthorizationTokenService
) {

    @PostMapping
    fun createOrderItem(
        @RequestBody request: List<OrderItem>,
        @RequestHeader(HttpHeaders.AUTHORIZATION) token: String
    ): List<UpsertResponse> {
        authService.validate(token)

        return sendOrderItemToERPUseCase.execute(request)
    }
}