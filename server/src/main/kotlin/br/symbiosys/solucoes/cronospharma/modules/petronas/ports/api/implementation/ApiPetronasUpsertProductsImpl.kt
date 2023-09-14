package br.symbiosys.solucoes.cronospharma.modules.petronas.ports.api.implementation

import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.KeyProductsInventory
import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.KeyProducts
import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.Products
import br.symbiosys.solucoes.cronospharma.modules.petronas.models.response.UpsertResponse
import br.symbiosys.solucoes.cronospharma.modules.petronas.ports.api.ApiPetronasUpsertProducts
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PutMapping

@FeignClient(name = "petronas-upsert-products", url = "\${app.petronas.base.url}")
interface ApiPetronasUpsertProductsImpl : ApiPetronasUpsertProducts {

    @PutMapping(value = ["/services/apexrest/ebMobile/erp2?functionName=UpsertProducts"])
    override fun upsertProducts(accounts: List<Products>): ResponseEntity<List<UpsertResponse>>

    @PutMapping(value = ["/services/apexrest/ebMobile/erp2?functionName=UpsertKeyProducts"])
    override fun upsertKeyProducts(accounts: List<KeyProducts>): ResponseEntity<List<UpsertResponse>>

    @PutMapping(value = ["/services/apexrest/ebMobile/erp2?functionName=UpsertKeyProductsInventory"])
    override fun upsertKeyProductsInventory(accounts: List<KeyProductsInventory>): ResponseEntity<List<UpsertResponse>>


}