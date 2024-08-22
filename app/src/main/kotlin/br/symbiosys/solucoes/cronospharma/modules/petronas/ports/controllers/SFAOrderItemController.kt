package br.symbiosys.solucoes.cronospharma.modules.petronas.ports.controllers

import br.symbiosys.solucoes.cronospharma.commons.AuthorizationTokenService
import br.symbiosys.solucoes.cronospharma.commons.ROTAS
import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.OrderItem
import br.symbiosys.solucoes.cronospharma.modules.petronas.models.response.UpsertResponse
import br.symbiosys.solucoes.cronospharma.modules.petronas.usecases.SendOrderItemToERPUseCase
import br.symbiosys.solucoes.cronospharma.modules.petronas.usecases.SendOrderItemToSFAUseCase
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping(ROTAS.PETRONAS_CADASTRAR_ITEM_PEDIDOS)
class SFAOrderItemController
    (
    private val sendOrderItemToERPUseCase: SendOrderItemToERPUseCase,
    private val sendOrderItemToSFAUseCase: SendOrderItemToSFAUseCase,
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

    @GetMapping
    fun searchOrderItem(): Any? {


        return sendOrderItemToSFAUseCase.execute("5000022928")
    }

}