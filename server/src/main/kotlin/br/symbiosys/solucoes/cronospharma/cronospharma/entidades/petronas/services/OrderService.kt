package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.services

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos.PedidoPalmRepository
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.request.Order
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.response.UpsertResponse
import org.slf4j.LoggerFactory

import org.springframework.stereotype.Service


@Service
class OrderService
    (private val pedidoPalmRepository: PedidoPalmRepository)
{

    val logger = LoggerFactory.getLogger(OrderService::class.java)
    fun createOrder(request: List<Order>): List<UpsertResponse> {

        val response = mutableListOf<UpsertResponse>()
        request.forEach {order ->
            run {
                try {
                    val pedido = SFAOrderToPedidoPalm.convert(order)
                    val pedidoPalm = pedidoPalmRepository.save(pedido)

                    response.add(UpsertResponse().apply {
                        isSuccess = true
                        sfdcId = order.salesForceId ?: ""
                        externalId = "${order.dtCode}-${pedidoPalm.NumPedidoPalm}"
                        errors = ""
                    })
                } catch (e: Exception) {
                    logger.error("erro ao adicionar pedido", e)
                    response.add(UpsertResponse().apply {
                        isSuccess = false
                        sfdcId = order.salesForceId ?: ""
                        externalId = "${order.dtCode}-${order.orderNumberSfa}"
                        errors = "erro ao adicionar pedido"
                    })

                }

            }
        }

        return response
    }

}
