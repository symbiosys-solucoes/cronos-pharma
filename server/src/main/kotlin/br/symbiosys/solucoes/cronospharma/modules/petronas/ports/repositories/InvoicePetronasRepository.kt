package br.symbiosys.solucoes.cronospharma.modules.petronas.ports.repositories

import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.Invoice

interface InvoicePetronasRepository {
    fun markAsUpdated(salesid: String)
    fun markAsCreated(codFilial: String, numero: String, salesid: String)
    fun findAll(enviados: Boolean = false): List<Invoice>



}
