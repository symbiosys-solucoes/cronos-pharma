package br.symbiosys.solucoes.cronospharma.modules.petronas.ports.api.implementation

import br.symbiosys.solucoes.cronospharma.modules.petronas.models.response.UpsertResponse
import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.Promotion
import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.PromotionFilterItem
import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.PromotionProducts
import br.symbiosys.solucoes.cronospharma.modules.petronas.ports.api.ApiPetronasUpsertPromotions
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PutMapping

@FeignClient(name = "petronas-upsert-promotions", url = "\${app.petronas.base.url}")
interface ApiPetronasUpsertPromotionsImpl : ApiPetronasUpsertPromotions {

    @PutMapping(value = ["/services/apexrest/ebMobile/erp2?functionName=UpsertPromotionSchemes"])
    override fun upsertPromotions(accounts: List<Promotion>): ResponseEntity<List<UpsertResponse>>

    @PutMapping(value = ["/services/apexrest/ebMobile/erp2?functionName=UpsertPromotionProducts"])
    override fun upsertPromotionProducts(accounts: List<PromotionProducts>): ResponseEntity<List<UpsertResponse>>

    @PutMapping(value = ["/services/apexrest/ebMobile/erp2?functionName=UpsertPromotionFilterItems"])
    override fun upsertPromotionFilterItems(accounts: List<PromotionFilterItem>): ResponseEntity<List<UpsertResponse>>


}