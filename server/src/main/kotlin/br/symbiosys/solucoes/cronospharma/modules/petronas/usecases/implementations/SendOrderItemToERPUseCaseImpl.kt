package br.symbiosys.solucoes.cronospharma.modules.petronas.usecases.implementations

import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.OrderItem
import br.symbiosys.solucoes.cronospharma.modules.petronas.models.response.UpsertResponse
import br.symbiosys.solucoes.cronospharma.modules.petronas.ports.repositories.PedidoPalmPetronasRepository
import br.symbiosys.solucoes.cronospharma.modules.petronas.usecases.SendOrderItemToERPUseCase
import br.symbiosys.solucoes.cronospharma.modules.petronas.usecases.converters.SFAOrderItemToItemPedidoPalmPetronas
import br.symbiosys.solucoes.cronospharma.sym.gateway.repository.SymParametrosRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component


@Component
class SendOrderItemToERPUseCaseImpl(
    private val pedidoPalmPetronasRepository: PedidoPalmPetronasRepository,
    private val symParametrosRepository: SymParametrosRepository
) : SendOrderItemToERPUseCase {


    val logger = LoggerFactory.getLogger(SendOrderItemToERPUseCaseImpl::class.java)

    override fun execute(request: List<OrderItem>): List<UpsertResponse> {
        val response = mutableListOf<UpsertResponse>()
        val symparams = symParametrosRepository.findAll()
        request.forEachIndexed { index, orderItem ->
            run {
                try {
                    val item = SFAOrderItemToItemPedidoPalmPetronas.convert(orderItem, index + 1)

                    logger.info("gravando item ${item.codigoProduto} no Cronos")
                    val itemPedido = pedidoPalmPetronasRepository.save(
                        item,
                        orderItem.orderNumberSfa!!,
                        symparams.find { it.codigoDistribuidorPetronas == orderItem.dtCode }!!.codigoFilial!!
                    )
                    logger.info("item gravado com sucesso no cronos id: ${itemPedido.idItemPedido}")
                    response.add(UpsertResponse().apply {
                        isSuccess = true
                        isCreated = true
                        sfdcId = orderItem.sfaOrderItemId ?: ""
                        externalId = "${orderItem.dtCode}-${itemPedido.idItemPedido}"
                        errors = ""
                    })

                } catch (e: Exception) {
                    logger.error("erro ao adicionar item", e)
                    response.add(UpsertResponse().apply {
                        isSuccess = false
                        isCreated = false
                        sfdcId = orderItem.sfaOrderItemId ?: ""
                        externalId = "${orderItem.dtCode}-${orderItem.orderNumberSfa}"
                    })
                }
            }
        }

        return response
    }

}
