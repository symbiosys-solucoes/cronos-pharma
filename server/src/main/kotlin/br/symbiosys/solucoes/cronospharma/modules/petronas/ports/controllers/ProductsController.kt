package br.symbiosys.solucoes.cronospharma.modules.petronas.ports.controllers

import br.symbiosys.solucoes.cronospharma.modules.petronas.usecases.SendProductInfoToSFAUseCase
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/petronas/envio")
class ProductsController (private val sendProductInfoToSFAUseCase: SendProductInfoToSFAUseCase) {

    @PostMapping("/produtos")
    fun sendProducts() {
        try {
            sendProductInfoToSFAUseCase.info()
        } catch (e: Exception) {
            throw InternalError(e.message)
        }
    }

    @PostMapping("/precos")
    fun sendPrecos() {
        try {
            sendProductInfoToSFAUseCase.prices()
        } catch (e: Exception) {
            throw InternalError(e.message)
        }
    }

    @PostMapping("/estoque")
    fun sendEstoque() {
        try {
            sendProductInfoToSFAUseCase.inventory()
        } catch (e: Exception) {
            throw InternalError(e.message)
        }
    }
}