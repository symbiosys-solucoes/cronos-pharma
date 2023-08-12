package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.api

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.request.*
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.response.UpsertResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PutMapping

@FeignClient(name = "petronas-upsert-promotions", url = "\${app.petronas.base.url}")
interface ApiPetronasUpsertPromotions {

    @PutMapping(value = ["/services/apexrest/ebMobile/erp2?functionName=UpsertPromotionSchemes"])
    fun upsertPromotions(accounts: List<Promotion>): ResponseEntity<List<UpsertResponse>>

    @PutMapping(value = ["/services/apexrest/ebMobile/erp2?functionName=UpsertPromotionProducts"])
    fun upsertPromotionProducts(accounts: List<PromotionProducts>): ResponseEntity<List<UpsertResponse>>

    @PutMapping(value = ["/services/apexrest/ebMobile/erp2?functionName=UpsertPromotionFilterItems"])
    fun upsertPromotionFilterItems(accounts: List<PromotionFilterItem>): ResponseEntity<List<UpsertResponse>>


}