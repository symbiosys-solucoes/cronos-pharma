package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.controllers

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.services.ProductsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/petronas/envio")
class ProductsController {

    @Autowired
    lateinit var productsService: ProductsService

    @PostMapping("/produtos")
    fun sendProducts() {
        try {
            productsService.sendProductsToSFA()
        } catch (e: Exception) {
            throw InternalError(e.message)
        }
    }

    @PostMapping("/precos")
    fun sendPrecos() {
        try {
            productsService.sendKeyProductsToSFA()
        } catch (e: Exception) {
            throw InternalError(e.message)
        }
    }

    @PostMapping("/estoque")
    fun sendEstoque() {
        try {
            productsService.sendKeyProductsInventoryToSFA()
        } catch (e: Exception) {
            throw InternalError(e.message)
        }
    }
}