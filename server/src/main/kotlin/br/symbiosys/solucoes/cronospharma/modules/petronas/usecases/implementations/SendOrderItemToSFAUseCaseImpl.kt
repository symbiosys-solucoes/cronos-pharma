package br.symbiosys.solucoes.cronospharma.modules.petronas.usecases.implementations

import br.symbiosys.solucoes.cronospharma.modules.petronas.ports.repositories.PedidoPalmPetronasRepository
import br.symbiosys.solucoes.cronospharma.modules.petronas.ports.api.ApiPetronasUpsertOrders
import br.symbiosys.solucoes.cronospharma.modules.petronas.usecases.SendOrderItemToSFAUseCase
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class SendOrderItemToSFAUseCaseImpl(
    private val apiPetronasUpsertOrders: ApiPetronasUpsertOrders,
    private val pedidoPalmPetronasRepository: PedidoPalmPetronasRepository
) : SendOrderItemToSFAUseCase {

    val logger = LoggerFactory.getLogger(SendOrderItemToSFAUseCase::class.java)
    override fun execute(numeroPedido: String) {
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
