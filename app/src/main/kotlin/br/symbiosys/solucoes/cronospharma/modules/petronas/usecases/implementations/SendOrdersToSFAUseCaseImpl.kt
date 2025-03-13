package br.symbiosys.solucoes.cronospharma.modules.petronas.usecases.implementations

import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.OrderRequest
import br.symbiosys.solucoes.cronospharma.modules.petronas.ports.api.ApiPetronasUpsertOrders
import br.symbiosys.solucoes.cronospharma.modules.petronas.ports.repositories.PedidoPalmPetronasRepository
import br.symbiosys.solucoes.cronospharma.modules.petronas.usecases.SendOrderItemToSFAUseCase
import br.symbiosys.solucoes.cronospharma.modules.petronas.usecases.SendOrdersToSFAUseCase
import br.symbiosys.solucoes.cronospharma.sym.gateway.repository.SymErrosRepository
import br.symbiosys.solucoes.cronospharma.sym.model.SymErros
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime

@Component
class SendOrdersToSFAUseCaseImpl(
    private val apiPetronasUpsertOrders: ApiPetronasUpsertOrders,
    private val pedidoPalmPetronasRepository: PedidoPalmPetronasRepository,
    private val sendOrderItemToSFAUseCase: SendOrderItemToSFAUseCase,
    private val symErrosRepository: SymErrosRepository,
) : SendOrdersToSFAUseCase {
    val logger = LoggerFactory.getLogger(SendOrdersToSFAUseCaseImpl::class.java)
    val mapper = ObjectMapper()

    override fun execute() {
        pedidoPalmPetronasRepository.findAll(enviados = false).forEach { pedidoCronos ->
            val response = apiPetronasUpsertOrders.upsertOrders(listOf(OrderRequest.from(pedidoCronos)))
            val erros = mutableListOf<SymErros>()
            response.body?.forEach {
                if (it.isSuccess) logger.info("pedido ${it.externalId} enviado com sucesso")
                if (it.isSuccess && it.isCreated) {
                    pedidoCronos.idCronos?.let { it1 -> pedidoPalmPetronasRepository.markAsSent(it1) }
                    pedidoCronos.orderNumberErp?.let { it1 -> sendOrderItemToSFAUseCase.execute(it1) }

                }
                if (it.isSuccess && !it.isCreated) {
                    pedidoCronos.idCronos?.let { it1 -> pedidoPalmPetronasRepository.markAsSent(it1) }
                    pedidoCronos.orderNumberErp?.let { it1 -> sendOrderItemToSFAUseCase.execute(it1) }
                }
                if (!it.isSuccess) {
                    logger.error("erro ao enviar pedido ${it.externalId}")
                    erros.add(
                        SymErros().apply {
                            dataOperacao = LocalDateTime.now()
                            tipoOperacao = "CADASTRO PEDIDO SFA"
                            petronasResponse = mapper.writeValueAsString(it)
                        },
                    )
                }
            }
            symErrosRepository.saveAll(erros)
        }
        logger.info("Concluido envio de Lote de Pedidos para SFA")
    }

    @Async
    override suspend fun executeAsync() {
        this.execute()
    }

    override fun execute(
        initialDate: LocalDate?,
        endDate: LocalDate?,
        erpOrderNumber: String?,
    ) {
        logger.info("Iniciando busca de pedidos para envio com os seguintes parametros: $initialDate - $endDate - $erpOrderNumber")
        pedidoPalmPetronasRepository.findAll(initialDate, endDate, erpOrderNumber).chunked(50).forEach {
            val response = apiPetronasUpsertOrders.upsertOrders(it.map { OrderRequest.from(it) }.toList())
            val erros = mutableListOf<SymErros>()
            response.body?.forEach {
                logger.info(it.toString())
                if (it.isSuccess && it.isCreated) {
                    val numPedido = it.externalId!!.split("-", limit = 2)[1]
                    val distribuidor = it.externalId!!.split("-", limit = 2)[0]
                    pedidoPalmPetronasRepository.markAsSent(numPedido, distribuidor = distribuidor)
                    sendOrderItemToSFAUseCase.execute(numPedido)
                }
                if (it.isSuccess && !it.isCreated) {
                    val numPedido = it.externalId!!.split("-", limit = 2)[1]
                    val distribuidor = it.externalId!!.split("-", limit = 2)[0]
                    pedidoPalmPetronasRepository.markAsSent(numPedido, distribuidor = distribuidor)
                    sendOrderItemToSFAUseCase.execute(numPedido)
                }
                if (!it.isSuccess) {
                    logger.error("erro ao enviar pedido ${it.externalId}")
                    erros.add(
                        SymErros().apply {
                            dataOperacao = LocalDateTime.now()
                            tipoOperacao = "CADASTRO PEDIDO SFA"
                            petronasResponse = mapper.writeValueAsString(it)
                        },
                    )
                }
            }
            symErrosRepository.saveAll(erros)
        }
    }

    @Async
    override suspend fun executeAsync(
        initialDate: LocalDate?,
        endDate: LocalDate?,
        erpOrderNumber: String?,
    ) {
        this.execute(initialDate, endDate, erpOrderNumber)
    }

    override fun delete(sfaOrderNumber: String) {
        logger.info("Deletando pedido $sfaOrderNumber")
        pedidoPalmPetronasRepository.deletePedidoPalmPetronas(sfaOrderNumber)
    }
}
