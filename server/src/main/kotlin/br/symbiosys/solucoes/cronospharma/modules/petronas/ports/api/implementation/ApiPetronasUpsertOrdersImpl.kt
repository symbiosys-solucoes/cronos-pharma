package br.symbiosys.solucoes.cronospharma.modules.petronas.ports.api.implementation

import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.OrderItem
import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.OrderRequest
import br.symbiosys.solucoes.cronospharma.modules.petronas.models.response.UpsertResponse
import br.symbiosys.solucoes.cronospharma.modules.petronas.ports.api.ApiPetronasUpsertOrders
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PutMapping

@FeignClient(name = "petronas-upsert-orders", url = "\${app.petronas.base.url}")
interface ApiPetronasUpsertOrdersImpl : ApiPetronasUpsertOrders {

    @PutMapping(value = ["/services/apexrest/ebMobile/erp2?functionName=UpsertOrders"])
    override fun upsertOrders(accounts: List<OrderRequest>): ResponseEntity<List<UpsertResponse>>

    @PutMapping(value = ["/services/apexrest/ebMobile/erp2?functionName=UpsertOrderItems"])
    override fun upsertOrderItems(accounts: List<OrderItem>): ResponseEntity<List<UpsertResponse>>


}