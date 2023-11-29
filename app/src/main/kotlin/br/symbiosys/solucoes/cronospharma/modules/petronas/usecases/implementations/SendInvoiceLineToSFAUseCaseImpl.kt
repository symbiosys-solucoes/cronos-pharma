package br.symbiosys.solucoes.cronospharma.modules.petronas.usecases.implementations

import br.symbiosys.solucoes.cronospharma.modules.petronas.ports.api.ApiPetronasInvoices
import br.symbiosys.solucoes.cronospharma.modules.petronas.ports.repositories.InvoiceLinePetronasRepository
import br.symbiosys.solucoes.cronospharma.modules.petronas.usecases.SendInvoiceLineToSFAUseCase
import br.symbiosys.solucoes.cronospharma.sym.gateway.repository.SymErrosRepository
import br.symbiosys.solucoes.cronospharma.sym.model.SymErros
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalDateTime


@Component
class SendInvoiceLineToSFAUseCaseImpl(
    private val apiPetronasInvoices: ApiPetronasInvoices,
    private val invoiceLinePetronasRepository: InvoiceLinePetronasRepository,
    private val symErrosRepository: SymErrosRepository
) : SendInvoiceLineToSFAUseCase {



    val logger = LoggerFactory.getLogger(SendInvoiceLineToSFAUseCaseImpl::class.java)
    val mapper = ObjectMapper()


    override fun  execute(salesId: String) {
        val erros = mutableListOf<SymErros>()
        invoiceLinePetronasRepository.findAll(salesId).chunked(50).forEach {
            val response = apiPetronasInvoices.upsertInvoiceLines(it)
            response.body?.forEach {
                if (it.isSuccess && it.isCreated) {
                    logger.info("Items da Nota ${it.externalId} enviado e criado com sucesso")
                    invoiceLinePetronasRepository.markAsCreated(it.externalId!!.toInt(), it.sfdcId!!)
                }
                if (it.isSuccess && !it.isCreated) {
                    logger.info("Items da Nota ${it.externalId} enviado e atualizado com sucesso")
                    invoiceLinePetronasRepository.markAsUpdated(it.externalId!!.toInt(), it.sfdcId!!)
                }
                if (!it.isSuccess) {
                    logger.error("erro ao enviar ITEM NOTA ${it.externalId}")
                    erros.add(SymErros().apply {
                        dataOperacao = LocalDateTime.now()
                        tipoOperacao = "CADASTRO NF ITEM SFA"
                        petronasResponse = mapper.writeValueAsString(it)
                    })
                }

            }
        }
        symErrosRepository.saveAll(erros)

    }


}
