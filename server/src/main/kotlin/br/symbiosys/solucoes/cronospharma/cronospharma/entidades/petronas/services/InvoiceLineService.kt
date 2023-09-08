package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.services

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.api.ApiPetronasInvoices
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.repositories.InvoiceLinePetronasRepository
import br.symbiosys.solucoes.cronospharma.cronospharma.sym.gateway.repository.SymErrosRepository
import br.symbiosys.solucoes.cronospharma.cronospharma.sym.model.SymErros
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime


@Service
class InvoiceLineService {


    @Autowired
    lateinit var apiPetronasInvoices: ApiPetronasInvoices

    @Autowired
    lateinit var invoiceLinePetronasRepository: InvoiceLinePetronasRepository


    @Autowired
    private lateinit var symErrosRepository: SymErrosRepository

    val logger = LoggerFactory.getLogger(InvoiceLineService::class.java)
    val mapper = ObjectMapper()


    fun sendInvoiceLineToSFA(salesId: String) {
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
