package br.symbiosys.solucoes.cronospharma.modules.petronas.usecases.implementations

import br.symbiosys.solucoes.cronospharma.modules.petronas.ports.api.ApiPetronasInvoices
import br.symbiosys.solucoes.cronospharma.modules.petronas.ports.repositories.InvoicePetronasRepository
import br.symbiosys.solucoes.cronospharma.modules.petronas.usecases.SendInvoiceLineToSFAUseCase
import br.symbiosys.solucoes.cronospharma.modules.petronas.usecases.SendInvoicesToSFAUseCase
import br.symbiosys.solucoes.cronospharma.sym.gateway.repository.SymErrosRepository
import br.symbiosys.solucoes.cronospharma.sym.model.SymErros
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class SendInvoicesToSFAUseCaseImpl(
    private val apiPetronasInvoices: ApiPetronasInvoices,
    private val invoicePetronasRepository: InvoicePetronasRepository,
    private val sendInvoiceLineToSFAUseCase: SendInvoiceLineToSFAUseCase,
    private val symErrosRepository: SymErrosRepository,
) : SendInvoicesToSFAUseCase {
    val logger = LoggerFactory.getLogger(SendInvoicesToSFAUseCaseImpl::class.java)
    val mapper = ObjectMapper()

    override fun execute() {
        val dataAtual = LocalDate.now()
        val seteDiasAtras = dataAtual.minusDays(1)
        this.execute(seteDiasAtras, dataAtual, null)
    }

    override fun execute(
        initialDate: LocalDate?,
        endDate: LocalDate?,
        erpInvoiceNumber: String?,
    ) {
        logger.info("Iniciando busca de pedidos para envio com os seguintes parametros: $initialDate - $endDate - $erpInvoiceNumber")
        val erros = mutableListOf<SymErros>()
        invoicePetronasRepository.findAll(initialDate, endDate, erpInvoiceNumber).chunked(100).forEach {
            val response = apiPetronasInvoices.upsertInvoices(it)
            response.body?.forEach {
                if (it.isSuccess && it.isCreated) {
                    logger.info("Nota ${it.externalId} enviado e criado com sucesso")
                    val nfInfo = it.externalId!!.split("-")
                    invoicePetronasRepository.markAsCreated(nfInfo[1], nfInfo[0], it.sfdcId!!)
                    sendInvoiceLineToSFAUseCase.execute(it.externalId!!)
                }
                if (it.isSuccess && !it.isCreated) {
                    logger.info("Nota ${it.externalId} enviado e atualizado com sucesso")
                    invoicePetronasRepository.markAsUpdated(it.sfdcId!!)
                    sendInvoiceLineToSFAUseCase.execute(it.externalId!!)
                }
                if (!it.isSuccess) {
                    logger.error("erro ao enviar NOTA ${it.externalId}")
                    erros.add(
                        SymErros().apply {
                            dataOperacao = LocalDateTime.now()
                            tipoOperacao = "CADASTRO NF SFA"
                            petronasResponse = mapper.writeValueAsString(it)
                        },
                    )
                }
            }
        }
        symErrosRepository.saveAll(erros)
    }
}
