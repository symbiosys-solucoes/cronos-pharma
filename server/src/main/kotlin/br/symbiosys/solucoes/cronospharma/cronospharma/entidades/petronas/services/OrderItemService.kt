package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.services

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.api.ApiPetronasUpsertOrders
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.request.OrderItem
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.response.UpsertResponse
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.repositories.PedidoPalmPetronasRepository
import br.symbiosys.solucoes.cronospharma.cronospharma.sym.gateway.repository.SymParametrosRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class OrderItemService {

    @Autowired
    lateinit var apiPetronasUpsertOrders: ApiPetronasUpsertOrders

    @Autowired
    lateinit var pedidoPalmPetronasRepository: PedidoPalmPetronasRepository

    @Autowired
    lateinit var symParametrosRepository: SymParametrosRepository

    val logger = LoggerFactory.getLogger(OrderItemService::class.java)

    fun createOrderItem(request: List<OrderItem>): List<UpsertResponse> {
        val response = mutableListOf<UpsertResponse>()
        val symparams = symParametrosRepository.findAll()
        request.forEachIndexed { index, orderItem ->
            run {
                try {
                    val item = SFAOrderItemToItemPedidoPalmPetronas.convert(orderItem,index + 1)

                    logger.info("gravando item ${item.codigoProduto} no Cronos")
                    val itemPedido = pedidoPalmPetronasRepository.save(item, orderItem.orderNumberSfa!!, symparams.find { it.codigoDistribuidorPetronas == orderItem.dtCode }!!.codigoFilial!!)
                    logger.info("item gravado com sucesso no cronos id: ${itemPedido.idItemPedido}")
                    response.add(UpsertResponse().apply {
                        isSuccess = true
                        isCreated = true
                        sfdcId = orderItem.orderItemNumberSfa ?: ""
                        externalId = "${orderItem.dtCode}-${itemPedido.idItemPedido}"
                        errors = ""
                    })

                } catch (e: Exception) {
                    logger.error("erro ao adicionar item", e)
                    response.add(UpsertResponse().apply {
                        isSuccess = false
                        isCreated = false
                        sfdcId = orderItem.orderItemNumberSfa ?: ""
                        externalId = "${orderItem.dtCode}-${orderItem.orderNumberSfa}"
                    })
                }
            }
        }

        return response
    }


    fun sendOrderItemToSFA(numeroPedido: String) {
        pedidoPalmPetronasRepository.findItems(numeroPedido).chunked(50).forEach {
            val response = apiPetronasUpsertOrders.upsertOrderItems(it)
            response.body?.forEach {
                if (it.isSuccess && it.isCreated) {
                    logger.info("Item ${it.externalId} enviado e criado com sucesso")
                    val numItem = it.externalId!!.split("-")[1].toInt()
                    pedidoPalmPetronasRepository.markAsSent(numItem, it.sfdcId!!)
                }
                if (it.isSuccess && !it.isCreated) {
                    logger.info("Item ${it.externalId} enviado e atualizado com sucesso")
                    val numItem = it.externalId!!.split("-")[1].toInt()
                    pedidoPalmPetronasRepository.markAsSent(numItem, it.sfdcId!!)
                }
                logger.error("erro ao enviar item ${it.externalId}")
            }
        }
    }
}
