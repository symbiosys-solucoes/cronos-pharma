package br.symbiosys.solucoes.cronospharma.modules.petronas.usecases.implementations

import br.symbiosys.solucoes.cronospharma.modules.petronas.ports.repositories.PedidoPalmPetronasRepository
import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.Order
import br.symbiosys.solucoes.cronospharma.modules.petronas.models.response.UpsertResponse
import br.symbiosys.solucoes.cronospharma.modules.petronas.usecases.SendOrderToERPUseCase
import br.symbiosys.solucoes.cronospharma.modules.petronas.usecases.converters.SFAOrderToPetronasPedidoPalm
import br.symbiosys.solucoes.cronospharma.sym.gateway.repository.SymParametrosRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
@EnableScheduling
class SendOrderToERPUseCaseImpl(
    private val symParametrosRepository: SymParametrosRepository,
    private val pedidoPalmPetronasRepository: PedidoPalmPetronasRepository
) : SendOrderToERPUseCase {
    val logger = LoggerFactory.getLogger(SendOrderToERPUseCase::class.java)

    override fun execute(request: List<Order>): List<UpsertResponse> {
        val parametros = symParametrosRepository.findAll()

        val response = mutableListOf<UpsertResponse>()
        request.forEach { order ->
            run {
                try {
                    val pedido = SFAOrderToPetronasPedidoPalm.convert(
                        order,
                        parametros.find { it.codigoDistribuidorPetronas == order.dtCode }!!
                    )
                    logger.info("gravando pedido ${pedido.numeroPedido} no Cronos")
                    val pedidoPalm = pedidoPalmPetronasRepository.save(pedido)
                    logger.info("pedido gravado com sucesso no Cronos id: ${pedido.idPedidoPalm}")
                    response.add(UpsertResponse().apply {
                        isSuccess = true
                        isCreated = true
                        sfdcId = order.salesForceId ?: ""
                        externalId = "${order.dtCode}-${pedidoPalm.numeroPedido}"
                        errors = ""
                    })

                } catch (e: Exception) {
                    logger.error("erro ao adicionar pedido", e)
                    response.add(UpsertResponse().apply {
                        isSuccess = false
                        isCreated = false
                        sfdcId = order.salesForceId ?: ""
                        externalId = "${order.dtCode}-${order.orderNumberSfa}"
                        errors = "erro ao adicionar pedido"
                    })

                }

            }
        }

        return response
    }

    @Scheduled(fixedDelay = 60 * 20 * 1000)
    fun convertOrderToMovimento() {
        pedidoPalmPetronasRepository.convertAll()
    }

}