package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.api

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.request.OrderItem
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.request.OrderRequest
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.response.UpsertResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PutMapping

@FeignClient(name = "petronas-upsert-orders", url = "\${app.petronas.base.url}")
interface ApiPetronasUpsertOrders {

    @PutMapping(value = ["/services/apexrest/ebMobile/erp2?functionName=UpsertOrders"])
    fun upsertOrders(accounts: List<OrderRequest>): ResponseEntity<List<UpsertResponse>>

    @PutMapping(value = ["/services/apexrest/ebMobile/erp2?functionName=UpsertOrderItems"])
    fun upsertOrderItems(accounts: List<OrderItem>): ResponseEntity<List<UpsertResponse>>


}