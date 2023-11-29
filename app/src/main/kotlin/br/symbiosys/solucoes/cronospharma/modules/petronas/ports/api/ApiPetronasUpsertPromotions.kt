package br.symbiosys.solucoes.cronospharma.modules.petronas.ports.api

import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.Promotion
import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.PromotionFilterItem
import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.PromotionProducts
import br.symbiosys.solucoes.cronospharma.modules.petronas.models.response.UpsertResponse
import org.springframework.http.ResponseEntity

interface ApiPetronasUpsertPromotions {
    fun upsertPromotions(accounts: List<Promotion>): ResponseEntity<List<UpsertResponse>>

    fun upsertPromotionProducts(accounts: List<PromotionProducts>): ResponseEntity<List<UpsertResponse>>

    fun upsertPromotionFilterItems(accounts: List<PromotionFilterItem>): ResponseEntity<List<UpsertResponse>>
}
