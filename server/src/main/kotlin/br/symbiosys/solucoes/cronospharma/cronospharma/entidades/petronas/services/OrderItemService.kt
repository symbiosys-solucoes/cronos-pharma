package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.services

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos.ItemPedidoPalmRepository
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos.PedidoPalmRepository
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.request.OrderItem
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.response.UpsertResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class OrderItemService {


    @Autowired
    lateinit var itemPedidoPalmRepository: ItemPedidoPalmRepository
    @Autowired
    lateinit var pedidoPalmRepository: PedidoPalmRepository

    val logger = LoggerFactory.getLogger(OrderItemService::class.java)

    fun createOrderItem(request: List<OrderItem>): List<UpsertResponse> {

        val response = mutableListOf<UpsertResponse>()

        request.forEachIndexed { index, orderItem ->
            run {
                val pedido = pedidoPalmRepository.findByOrigemAndNumPedidoPalm("PETRONAS", orderItem.orderNumberSfa?:"")
                if (pedido != null) {
                    try {
                        val itemPedidoPalm = itemPedidoPalmRepository.save(SFAOrderItemToPedidoPalm.convert(orderItem, pedido.IdPedidoPalm!!, index + 1), pedido)
                        response.add(UpsertResponse().apply {
                            isSuccess = true
                            sfdcId = orderItem.sfaOrderItemId ?: ""
                            externalId = "${orderItem.dtCode}-${itemPedidoPalm.IdItemPedidoPalm}"
                            errors = ""
                        })
                    } catch (e: Exception) {
                        logger.error("Erro ao adicionar item no ERP", e)
                        response.add(UpsertResponse().apply {
                            isSuccess = false
                            sfdcId = orderItem.sfaOrderItemId ?: ""
                            errors = "erro ao adicionar item no ERP"
                            externalId = "${orderItem.dtCode}-"
                        })
                    }
                } else {
                    response.add(UpsertResponse().apply {
                        isSuccess = false
                        sfdcId = orderItem.sfaOrderItemId ?: ""
                        errors = "Order not found"
                        externalId = "${orderItem.dtCode}-"
                    })
                }

            }
        }

        return response
    }

}
