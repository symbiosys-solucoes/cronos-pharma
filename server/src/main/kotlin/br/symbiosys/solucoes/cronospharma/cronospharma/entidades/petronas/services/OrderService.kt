package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.services

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.api.ApiPetronasUpsertOrders
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.request.Order
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.request.OrderRequest
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.response.UpsertResponse
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.repositories.PedidoPalmPetronasRepository
import br.symbiosys.solucoes.cronospharma.cronospharma.sym.gateway.repository.SymParametrosRepository
import br.symbiosys.solucoes.cronospharma.cronospharma.sym.model.SymParametros
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service


@Service
@EnableScheduling
class OrderService {
    @Autowired
    lateinit var symParametrosRepository: SymParametrosRepository

    @Autowired
    lateinit var apiPetronasUpsertOrders: ApiPetronasUpsertOrders

    @Autowired
    lateinit var pedidoPalmPetronasRepository: PedidoPalmPetronasRepository

    val logger = LoggerFactory.getLogger(OrderService::class.java)

    fun createOrderERP(request: List<Order>): List<UpsertResponse> {

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

    @Scheduled(fixedDelay = 60 * 15 * 1000)
    fun sendOrderToSFA() {
        pedidoPalmPetronasRepository.findAll(enviados = false).chunked(50).forEach {
            val response = apiPetronasUpsertOrders.upsertOrders(it.map { OrderRequest.from(it) }.toList())
            response.body?.forEach {
                if (it.isSuccess && it.isCreated) {
                    logger.info("Pedido ${it.externalId} enviado e criado com sucesso")
                    pedidoPalmPetronasRepository.markAsSent(it.externalId!!.split("-")[1])
                }
                if (it.isSuccess && !it.isCreated) {
                    logger.info("Pedido ${it.externalId} enviado e atualizado com sucesso")
                    pedidoPalmPetronasRepository.markAsSent(it.externalId!!.split("-")[1], true)
                }
                logger.error("erro ao enviar pedido ${it.externalId}")
            }
        }

    }

}
