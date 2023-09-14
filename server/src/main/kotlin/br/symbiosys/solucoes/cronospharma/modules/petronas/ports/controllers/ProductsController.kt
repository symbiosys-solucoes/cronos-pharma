package br.symbiosys.solucoes.cronospharma.modules.petronas.ports.controllers

import br.symbiosys.solucoes.cronospharma.commons.ROTAS
import br.symbiosys.solucoes.cronospharma.modules.petronas.usecases.SendProductInfoToSFAUseCase
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ProductsController (private val sendProductInfoToSFAUseCase: SendProductInfoToSFAUseCase) {

    @PostMapping(ROTAS.PETRONAS_ENVIO_PRODUTOS)
    fun sendProducts() {
        try {
            sendProductInfoToSFAUseCase.info()
        } catch (e: Exception) {
            throw InternalError(e.message)
        }
    }

    @PostMapping(ROTAS.PETRONAS_ENVIO_PRECOS)
    fun sendPrecos() {
        try {
            sendProductInfoToSFAUseCase.prices()
        } catch (e: Exception) {
            throw InternalError(e.message)
        }
    }

    @PostMapping(ROTAS.PETRONAS_ENVIO_ESTOQUE)
    fun sendEstoque() {
        try {
            sendProductInfoToSFAUseCase.inventory()
        } catch (e: Exception) {
            throw InternalError(e.message)
        }
    }
}