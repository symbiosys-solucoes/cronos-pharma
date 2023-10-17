package br.symbiosys.solucoes.cronospharma.modules.petronas.usecases.implementations

import br.symbiosys.solucoes.cronospharma.modules.petronas.ports.api.ApiPetronasUpsertOrders
import br.symbiosys.solucoes.cronospharma.modules.petronas.ports.repositories.PedidoPalmPetronasRepository
import br.symbiosys.solucoes.cronospharma.modules.petronas.usecases.SendOrderItemToSFAUseCase
import br.symbiosys.solucoes.cronospharma.sym.gateway.repository.SymErrosRepository
import br.symbiosys.solucoes.cronospharma.sym.model.SymErros
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class SendOrderItemToSFAUseCaseImpl(
    private val apiPetronasUpsertOrders: ApiPetronasUpsertOrders,
    private val pedidoPalmPetronasRepository: PedidoPalmPetronasRepository,
    private val symErrosRepository: SymErrosRepository,
) : SendOrderItemToSFAUseCase {

    val mapper = ObjectMapper()
    val logger = LoggerFactory.getLogger(SendOrderItemToSFAUseCase::class.java)
    override fun execute(numeroPedido: String) {
        pedidoPalmPetronasRepository.findItems(numeroPedido).chunked(50).forEach {
            val response = apiPetronasUpsertOrders.upsertOrderItems(it)
            val erros = mutableListOf<SymErros>()
            response.body?.forEach {
                if (it.isSuccess && it.isCreated) {
                    logger.info("Item ${it.externalId} enviado e criado com sucesso")
                    val numItem = it.externalId!!.split("-")[1].toInt()
                    pedidoPalmPetronasRepository.markAsSent(numItem, it.sfdcId!!)
                }
                if (it.isSuccess && !it.isCreated) {
                    logger.info("Item ${it.externalId} enviado e atualizado com sucesso")
                    if(it.externalId != null){
                        val numItem = it.externalId!!.split("-")[1].toInt()
                        pedidoPalmPetronasRepository.markAsSent(numItem, it.sfdcId!!)
                    }

                }
                if (!it.isSuccess) {
                    logger.error("erro ao enviar item ${it.externalId}")
                    erros.add(SymErros().apply {
                        dataOperacao = LocalDateTime.now()
                        tipoOperacao = "CADASTRO PRODUTO SFA"
                        petronasResponse = mapper.writeValueAsString(it)
                    })
                }

            }
            symErrosRepository.saveAll(erros)
        }
    }
}
