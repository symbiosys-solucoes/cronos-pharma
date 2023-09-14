package br.symbiosys.solucoes.cronospharma.modules.petronas.ports.repositories

import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.KeyProducts
import br.symbiosys.solucoes.cronospharma.modules.petronas.ports.repositories.implementations.ProductsKeyPetronasRepositoryImpl

interface ProductsKeyPetronasRepository {

    fun findAll(): MutableList<KeyProducts>

    fun markAsCreated(codProduto: String)

    fun markAsUpdated(codProduto: String)
}
