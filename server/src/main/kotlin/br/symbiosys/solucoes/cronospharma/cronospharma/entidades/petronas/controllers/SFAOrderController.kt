package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.controllers

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.request.Order
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.response.UpsertResponse
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.services.AuthorizationTokenService
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.services.OrderService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/petronas/Sfa/BR/Order")
class SFAOrderController
    (private val orderService: OrderService) {

    @Autowired
    lateinit var authService: AuthorizationTokenService


    @PostMapping
    fun createOrder(
        @RequestBody request: List<Order>,
        @RequestHeader(HttpHeaders.AUTHORIZATION) token: String
    ): List<UpsertResponse> {
        authService.validate(token)
        return orderService.createOrder(request)
    }

}
