package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.services

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.api.ApiPetronasInvoices
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.api.ApiPetronasUpsertOrders
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.request.OrderRequest
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.repositories.InvoiceLinePetronasRepository
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.repositories.InvoicePetronasRepository
import br.symbiosys.solucoes.cronospharma.cronospharma.sym.gateway.repository.SymErrosRepository
import br.symbiosys.solucoes.cronospharma.cronospharma.sym.model.SymErros
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime


@Service
@EnableScheduling
class InvoiceService {


    @Autowired
    lateinit var apiPetronasInvoices: ApiPetronasInvoices

    @Autowired
    lateinit var invoicePetronasRepository: InvoicePetronasRepository

    @Autowired
    lateinit var invoiceLineService: InvoiceLineService

    @Autowired
    private lateinit var symErrosRepository: SymErrosRepository

    val logger = LoggerFactory.getLogger(InvoiceService::class.java)
    val mapper = ObjectMapper()


    @Scheduled(fixedDelay = 60 * 15 * 1000)
    fun sendInvoiceToSFA() {
        val erros = mutableListOf<SymErros>()
        invoicePetronasRepository.findAll(enviados = false).chunked(50).forEach {
            val response = apiPetronasInvoices.upsertInvoices(it)
            response.body?.forEach {
                if (it.isSuccess && it.isCreated) {
                    logger.info("Nota ${it.externalId} enviado e criado com sucesso")
                    val nfInfo = it.externalId!!.split("|")
                    invoicePetronasRepository.markAsCreated(nfInfo[1], nfInfo[0], it.sfdcId!!)
                    invoiceLineService.sendInvoiceLineToSFA(it.sfdcId!!)
                }
                if (it.isSuccess && !it.isCreated) {
                    logger.info("Nota ${it.externalId} enviado e atualizado com sucesso")
                    invoicePetronasRepository.markAsUpdated(it.sfdcId!!)
                    invoiceLineService.sendInvoiceLineToSFA(it.sfdcId!!)
                }
                if (!it.isSuccess) {
                    logger.error("erro ao enviar NOTA ${it.externalId}")
                    erros.add(SymErros().apply {
                        dataOperacao = LocalDateTime.now()
                        tipoOperacao = "CADASTRO NF SFA"
                        petronasResponse = mapper.writeValueAsString(it)
                    })
                }

            }
        }
        symErrosRepository.saveAll(erros)

    }



}
