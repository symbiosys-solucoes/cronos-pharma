package br.symbiosys.solucoes.cronospharma.modules.petronas.ports.controllers

import br.symbiosys.solucoes.cronospharma.commons.ROTAS
import br.symbiosys.solucoes.cronospharma.modules.petronas.usecases.SendOrdersToSFAUseCase
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(ROTAS.PETRONAS_ENVIO_PEDIDOS)
class OrdersController (private val sendOrdersToSFAUseCase: SendOrdersToSFAUseCase) {


    @PostMapping
    fun send() {
        try {
            sendOrdersToSFAUseCase.execute()
        } catch (e: Exception) {
            e.printStackTrace()
            throw InternalError(e.message)
        }

    }
}