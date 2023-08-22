package br.symbiosys.solucoes.cronospharma.cronospharma.sym.service

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos.PedidoPalm
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos.PedidoPalmRepository
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.api.ApiPetronasUpsertOrders
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.request.OrderRequest
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.response.UpsertResponse
import br.symbiosys.solucoes.cronospharma.cronospharma.sym.gateway.repository.SymCustomerRepository
import br.symbiosys.solucoes.cronospharma.cronospharma.sym.model.ISymEventos
import br.symbiosys.solucoes.cronospharma.cronospharma.sym.model.SymEventos
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class MovimentosEventosService : ISymEventos {

    private val logger = LoggerFactory.getLogger(MovimentosEventosService::class.java)

    @Value("\${app.petronas.dtcode}")
    lateinit var petronasDtCode: String

    @Autowired
    lateinit var pedidoPalmRepository: PedidoPalmRepository

    @Autowired
    lateinit var symCustomerRepository: SymCustomerRepository

    @Autowired
    lateinit var apiPetronasUpsertOrders: ApiPetronasUpsertOrders

    override fun processarEventos(eventos: MutableList<SymEventos>): MutableList<OrderRequest> {
        val mapper = ObjectMapper()
        val registros = filtrarRegistros(eventos)
        val ordersToSend = mutableListOf<OrderRequest>()
        for (registro in registros) {
            val node: JsonNode = mapper.readTree(registro.newRegisterAsJson)
            val idPedido = node.get("IdPedidoPalm").asInt()
            val pedido = pedidoPalmRepository.findById(idPedido.toLong())

            if (pedido != null) {
                when (pedido.Origem) {
                    "PETRONAS" -> ordersToSend.add(toPetronasOrder(node, pedido))
                }
            }
        }
        val sfaResponses = mutableListOf<UpsertResponse>()
        if(ordersToSend.size > 50) {
            val listas = ordersToSend.chunked(50).toList()
            listas.forEach {
                val response = apiPetronasUpsertOrders.upsertOrders(it)
                if (response.statusCode == HttpStatus.OK) {
                    sfaResponses.addAll(response.body!!)
                }
            }
        } else {
            val response = apiPetronasUpsertOrders.upsertOrders(ordersToSend)
            if (response.statusCode == HttpStatus.OK) {
                sfaResponses.addAll(response.body!!)
            }
        }

        return ordersToSend
    }

    private fun toPetronasOrder(movimento: JsonNode, pedido: PedidoPalm) : OrderRequest {

        val customer = symCustomerRepository.findByCodigoCronos(movimento.get("CodCliFor").asText())
        if (customer.isPresent) {
            var statusSales = ""
            var statusMovimento = movimento.get("Status").asText()
            when (statusMovimento) {
                "C" -> statusSales = "Cancelado"
                "B" -> statusSales = "Bloqueado"
                else -> statusSales = "Faturado"
            }
            return OrderRequest().apply {
                orderNumberSfa = pedido.NumPedidoPalm
                orderNumberErp = pedido.NumPedidoCRONOS
                orderSource = "SFA Mobile"
                type = "Sales Order"
                dtCode = petronasDtCode
                accountNumber = customer.get().codigoIntegrador
                status = statusSales
                orderDate = LocalDateTime.parse(movimento.get("DtMov").asText())
                totalQuantity = pedido.itens.sumOf { it.Qtd }
                confirmedQuantity = pedido.itens.sumOf { it.QtdConfirmada ?: 0.0 }
                orderTotalVolume = movimento.get("QuantidadeVolumes").asDouble(0.0)
                netAmount = movimento.get("TotMov").asDouble(0.0)
                totalAmount = movimento.get("TotMov").asDouble(0.0)
                paymentMethod = movimento.get("Portador").asText()
                paymentKeyTerms = movimento.get("CondPag").asText()
                destination = ""
                deliveryType = "FOB"
                customerOrderNumber = pedido.NumPedidoPalmAux
                active = if(statusMovimento == "C") false else true
            }
        }
        throw RuntimeException("SymCustomer not found")
    }
}