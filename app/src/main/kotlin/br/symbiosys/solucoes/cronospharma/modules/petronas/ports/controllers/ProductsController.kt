package br.symbiosys.solucoes.cronospharma.modules.petronas.ports.controllers

import br.symbiosys.solucoes.cronospharma.commons.ROTAS
import br.symbiosys.solucoes.cronospharma.modules.petronas.usecases.SendProductInfoToSFAUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ProductsController (private val sendProductInfoToSFAUseCase: SendProductInfoToSFAUseCase) {

    @PostMapping(ROTAS.PETRONAS_ENVIO_PRODUTOS)
    suspend fun sendProducts(): ResponseDtoProduct {
        try {
            sendProductInfoToSFAUseCase.infoAsync(full = true)
            return ResponseDtoProduct("Enviando Carga de produtos para a Petronas")
        } catch (e: Exception) {
            throw InternalError(e.message)
        }
    }

    @PostMapping(ROTAS.PETRONAS_ENVIO_PRECOS)
    suspend fun sendPrecos(): ResponseDtoProduct {
        try {
            sendProductInfoToSFAUseCase.pricesAsync()
            return ResponseDtoProduct("Enviando Carga de precos para a Petronas")
        } catch (e: Exception) {
            throw InternalError(e.message)
        }
    }

    @PostMapping(ROTAS.PETRONAS_ENVIO_ESTOQUE)
    suspend fun sendEstoque(): ResponseDtoProduct {
        try {
            sendProductInfoToSFAUseCase.inventoryAsync()
            return ResponseDtoProduct("Enviando Carga de estoque para a Petronas")
        } catch (e: Exception) {
            throw InternalError(e.message)
        }
    }
}

data class ResponseDtoProduct(val message: String)