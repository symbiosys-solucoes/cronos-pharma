package br.symbiosys.solucoes.cronospharma.modules.petronas.ports.controllers

import br.symbiosys.solucoes.cronospharma.commons.ROTAS
import br.symbiosys.solucoes.cronospharma.modules.petronas.usecases.SendInvoicesToSFAUseCase
import br.symbiosys.solucoes.cronospharma.modules.petronas.usecases.SendOrdersToSFAUseCase
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping
class InvoicesController (private val sendInvoicesToSFAUseCase: SendInvoicesToSFAUseCase) {

    @PostMapping(ROTAS.PETRONAS_ENVIO_NOTAS)
    suspend fun send(@RequestBody request: InvoiceSenderRequest?): InvoicesSenderResponse {
        try {
            sendInvoicesToSFAUseCase.execute(request?.initialDate, request?.endDate, request?.erpOrderNumber)
            return InvoicesSenderResponse("Enviando carga de pedidos para PETRONAS")
        } catch (e: Exception) {
            e.printStackTrace()
            throw InternalError(e.message)
        }

    }
}

data class InvoicesSenderResponse(val message: String)


data class InvoiceSenderRequest(
    @JsonProperty("data_inicial")
    @JsonFormat(pattern = "dd/MM/yyyy")
    val initialDate: LocalDate?,
    @JsonProperty("data_final")
    @JsonFormat(pattern = "dd/MM/yyyy")
    val endDate: LocalDate?,
    @JsonProperty("numero_nota_cronos")
    val erpOrderNumber: String?,
    @JsonProperty("numero_pedido_petronas")
    val sfaOrderNumber: String?
)
