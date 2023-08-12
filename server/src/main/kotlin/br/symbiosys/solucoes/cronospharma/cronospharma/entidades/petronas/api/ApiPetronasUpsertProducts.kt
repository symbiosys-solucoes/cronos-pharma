package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.api

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.request.KeyProducts
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.request.Products
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.response.UpsertResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PutMapping

@FeignClient(name = "petronas-upsert-products", url = "\${app.petronas.base.url}")
interface ApiPetronasUpsertProducts {

    @PutMapping(value = ["/services/apexrest/ebMobile/erp2?functionName=UpsertProducts"])
    fun upsertProducts(accounts: List<Products>): ResponseEntity<List<UpsertResponse>>

    @PutMapping(value = ["/services/apexrest/ebMobile/erp2?functionName=UpsertKeyProducts"])
    fun upsertKeyProducts(accounts: List<KeyProducts>): ResponseEntity<List<UpsertResponse>>


}