package br.symbiosys.solucoes.cronospharma.modules.petronas.ports.api

import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.OrderItem
import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.OrderRequest
import br.symbiosys.solucoes.cronospharma.modules.petronas.models.response.UpsertResponse
import org.springframework.http.ResponseEntity

interface ApiPetronasUpsertOrders {

    fun upsertOrders(accounts: List<OrderRequest>): ResponseEntity<List<UpsertResponse>>

    fun upsertOrderItems(accounts: List<OrderItem>): ResponseEntity<List<UpsertResponse>>

}
