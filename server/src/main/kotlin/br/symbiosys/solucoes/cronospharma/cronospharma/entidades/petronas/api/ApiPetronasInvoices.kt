package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.api

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.request.Invoice
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.request.InvoiceLine
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.request.Order
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.request.OrderItem
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.response.UpsertResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PutMapping

@FeignClient(name = "petronas-upsert-invoices", url = "\${app.petronas.base.url}")
interface ApiPetronasInvoices {

    @PutMapping(value = ["/services/apexrest/ebMobile/erp2?functionName=UpsertInvoices"])
    fun upsertInvoices(accounts: List<Invoice>): ResponseEntity<List<UpsertResponse>>

    @PutMapping(value = ["/services/apexrest/ebMobile/erp2?functionName=UpsertInvoiceLines"])
    fun upsertInvoiceLines(accounts: List<InvoiceLine>): ResponseEntity<List<UpsertResponse>>


}