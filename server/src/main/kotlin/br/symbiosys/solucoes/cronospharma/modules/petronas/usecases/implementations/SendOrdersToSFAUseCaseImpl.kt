package br.symbiosys.solucoes.cronospharma.modules.petronas.usecases.implementations

import br.symbiosys.solucoes.cronospharma.modules.petronas.ports.repositories.PedidoPalmPetronasRepository
import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.OrderRequest
import br.symbiosys.solucoes.cronospharma.modules.petronas.ports.api.ApiPetronasUpsertOrders
import br.symbiosys.solucoes.cronospharma.modules.petronas.usecases.SendOrderItemToSFAUseCase
import br.symbiosys.solucoes.cronospharma.modules.petronas.usecases.SendOrdersToSFAUseCase
import br.symbiosys.solucoes.cronospharma.sym.gateway.repository.SymErrosRepository
import br.symbiosys.solucoes.cronospharma.sym.model.SymErros
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
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
        pedidoPalmPetronasRepository.findAll(enviados = false).chunked(50).forEach {
            val response = apiPetronasUpsertOrders.upsertOrders(it.map { OrderRequest.from(it) }.toList())
            val erros = mutableListOf<SymErros>()
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
                if (!it.isSuccess) {
                    logger.error("erro ao enviar pedido ${it.externalId}")
                    erros.add(SymErros().apply {
                        dataOperacao = LocalDateTime.now()
                        tipoOperacao = "CADASTRO PEDIDO SFA"
                        petronasResponse = mapper.writeValueAsString(it)
                    })
                }

            }
            symErrosRepository.saveAll(erros)
        }
    }

    @Async
    override suspend fun executeAsync() {
        this.execute()
    }

    override fun execute(initialDate: LocalDate?, endDate: LocalDate?, erpOrderNumber: String?) {
        logger.info("Iniciando busca de pedidos para envio com os seguintes parametros: ${initialDate} - ${endDate} - ${erpOrderNumber}")
        pedidoPalmPetronasRepository.findAll(initialDate, endDate, erpOrderNumber).chunked(50).forEach {
            logger.info("Enviando pedidos para SFA")
            val response = apiPetronasUpsertOrders.upsertOrders(it.map { OrderRequest.from(it) }.toList())
            logger.info("Enviado pedidos para SFA", response)
            val erros = mutableListOf<SymErros>()
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
                if (!it.isSuccess) {
                    logger.error("erro ao enviar pedido ${it.externalId}")
                    erros.add(SymErros().apply {
                        dataOperacao = LocalDateTime.now()
                        tipoOperacao = "CADASTRO PEDIDO SFA"
                        petronasResponse = mapper.writeValueAsString(it)
                    })
                }

            }
            symErrosRepository.saveAll(erros)
        }
    }

    @Async
    override suspend fun executeAsync(initialDate: LocalDate?, endDate: LocalDate?, erpOrderNumber: String?) {
        this.execute(initialDate, endDate, erpOrderNumber)
    }

    override fun delete(sfaOrderNumber: String) {
        logger.info("Deletando pedido $sfaOrderNumber")
        pedidoPalmPetronasRepository.deletePedidoPalmPetronas(sfaOrderNumber)
    }

}
