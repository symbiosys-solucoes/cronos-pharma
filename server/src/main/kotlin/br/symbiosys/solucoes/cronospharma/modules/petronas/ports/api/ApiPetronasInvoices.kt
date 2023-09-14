package br.symbiosys.solucoes.cronospharma.modules.petronas.ports.api

import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.Invoice
import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.InvoiceLine
import br.symbiosys.solucoes.cronospharma.modules.petronas.models.response.UpsertResponse
import org.springframework.http.ResponseEntity

interface ApiPetronasInvoices {
    fun upsertInvoices(accounts: List<Invoice>): ResponseEntity<List<UpsertResponse>>

    fun upsertInvoiceLines(accounts: List<InvoiceLine>): ResponseEntity<List<UpsertResponse>>
}
