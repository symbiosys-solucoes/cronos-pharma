package br.symbiosys.solucoes.cronospharma.modules.petronas.ports.repositories

import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.InvoiceLine

interface InvoiceLinePetronasRepository {

    fun markAsUpdated(idItem: Int, salesid: String)

    fun markAsCreated(idItem: Int, salesid: String)

    fun findAll(salesid: String): List<InvoiceLine>

}
