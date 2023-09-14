package br.symbiosys.solucoes.cronospharma.modules.petronas.usecases.implementations

import br.symbiosys.solucoes.cronospharma.modules.petronas.ports.repositories.PedidoPalmPetronasRepository
import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.OrderRequest
import br.symbiosys.solucoes.cronospharma.modules.petronas.ports.api.ApiPetronasUpsertOrders
import br.symbiosys.solucoes.cronospharma.modules.petronas.usecases.SendOrderItemToSFAUseCase
import br.symbiosys.solucoes.cronospharma.modules.petronas.usecases.SendOrdersToSFAUseCase
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component


@Component
@EnableScheduling
class SendOrdersToSFAUseCaseImpl(
    private val apiPetronasUpsertOrders: ApiPetronasUpsertOrders,
    private val pedidoPalmPetronasRepository: PedidoPalmPetronasRepository,
    private val sendOrderItemToSFAUseCase: SendOrderItemToSFAUseCase
) : SendOrdersToSFAUseCase {
    val logger = LoggerFactory.getLogger(SendOrdersToSFAUseCaseImpl::class.java)


    @Scheduled(cron = "\${app.cron.petronas.envia.pedidos}")
    override fun execute() {
        pedidoPalmPetronasRepository.findAll(enviados = false).chunked(50).forEach {
            val response = apiPetronasUpsertOrders.upsertOrders(it.map { OrderRequest.from(it) }.toList())
            response.body?.forEach {
                if (it.isSuccess && it.isCreated) {
                    logger.info("Pedido ${it.externalId} enviado e criado com sucesso")
                    val numPedido = it.externalId!!.split("-")[1]
                    pedidoPalmPetronasRepository.markAsSent(numPedido)
                    sendOrderItemToSFAUseCase.execute(numPedido)
                }
                if (it.isSuccess && !it.isCreated) {
                    logger.info("Pedido ${it.externalId} enviado e atualizado com sucesso")
                    val numPedido = it.externalId!!.split("-")[1]
                    pedidoPalmPetronasRepository.markAsSent(numPedido)
                    sendOrderItemToSFAUseCase.execute(numPedido)
                }
                logger.error("erro ao enviar pedido ${it.externalId}")
            }
        }

    }

}
