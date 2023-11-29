package br.symbiosys.solucoes.cronospharma.modules.petronas.ports.api

import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.KeyProducts
import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.KeyProductsInventory
import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.Products
import br.symbiosys.solucoes.cronospharma.modules.petronas.models.response.UpsertResponse
import org.springframework.http.ResponseEntity

interface ApiPetronasUpsertProducts {
    fun upsertProducts(accounts: List<Products>): ResponseEntity<List<UpsertResponse>>

    fun upsertKeyProducts(accounts: List<KeyProducts>): ResponseEntity<List<UpsertResponse>>
    fun upsertKeyProductsInventory(accounts: List<KeyProductsInventory>): ResponseEntity<List<UpsertResponse>>


}
