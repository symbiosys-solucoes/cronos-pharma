package br.symbiosys.solucoes.cronospharma.modules.petronas.ports.api.implementation

import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.Invoice
import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.InvoiceLine
import br.symbiosys.solucoes.cronospharma.modules.petronas.models.response.UpsertResponse
import br.symbiosys.solucoes.cronospharma.modules.petronas.ports.api.ApiPetronasInvoices
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PutMapping

@FeignClient(name = "petronas-upsert-invoices", url = "\${app.petronas.base.url}")
interface ApiPetronasInvoicesImpl : ApiPetronasInvoices {

    @PutMapping(value = ["/services/apexrest/ebMobile/erp2?functionName=UpsertInvoices"])
    override fun upsertInvoices(accounts: List<Invoice>): ResponseEntity<List<UpsertResponse>>

    @PutMapping(value = ["/services/apexrest/ebMobile/erp2?functionName=UpsertInvoiceLines"])
    override fun upsertInvoiceLines(accounts: List<InvoiceLine>): ResponseEntity<List<UpsertResponse>>


}