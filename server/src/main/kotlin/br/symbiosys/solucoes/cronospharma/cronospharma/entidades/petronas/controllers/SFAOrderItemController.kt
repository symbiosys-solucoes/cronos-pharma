package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.controllers

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.request.OrderItem
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.response.UpsertResponse
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.services.AuthorizationTokenService
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.services.OrderItemService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/petronas/Sfa/BR/OrderItem")
class SFAOrderItemController {


    @Autowired
    lateinit var orderItemService: OrderItemService

    @Autowired
    lateinit var authService: AuthorizationTokenService

    @PostMapping
    fun createOrderItem(
        @RequestBody request: List<OrderItem>,
        @RequestHeader(HttpHeaders.AUTHORIZATION) token: String
    ): List<UpsertResponse> {
        authService.validate(token)

        return orderItemService.createOrderItem(request)
    }
}