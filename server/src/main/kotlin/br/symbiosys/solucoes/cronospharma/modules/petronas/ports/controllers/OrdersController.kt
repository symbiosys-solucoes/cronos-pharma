package br.symbiosys.solucoes.cronospharma.modules.petronas.ports.controllers

import br.symbiosys.solucoes.cronospharma.commons.ROTAS
import br.symbiosys.solucoes.cronospharma.modules.petronas.usecases.SendOrdersToSFAUseCase
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping(ROTAS.PETRONAS_ENVIO_PEDIDOS)
class OrdersController (private val sendOrdersToSFAUseCase: SendOrdersToSFAUseCase) {


    @PostMapping
    suspend fun send(@RequestBody request: OrdersSenderRequest): OrdersSenderResponse {
        try {
            sendOrdersToSFAUseCase.executeAsync(request.initialDate, request.endDate, request.erpOrderNumber)
            return OrdersSenderResponse("Enviando carga de pedidos para PETRONAS")
        } catch (e: Exception) {
            e.printStackTrace()
            throw InternalError(e.message)
        }

    }
}
data class OrdersSenderResponse(val message: String)


data class OrdersSenderRequest(
    @JsonProperty("data_inicial")
    @JsonFormat(pattern = "dd/MM/yyyy")
    val initialDate: LocalDate?,
    @JsonProperty("data_final")
    @JsonFormat(pattern = "dd/MM/yyyy")
    val endDate: LocalDate?,
    @JsonProperty("numero_pedido_cronos")
    val erpOrderNumber: String?
)
