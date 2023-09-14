package br.symbiosys.solucoes.cronospharma.modules.petronas.ports.repositories

import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.Products

interface ProductsPetronasRepository {

    fun findAll(): MutableList<Products>

    fun markAsCreated(codProduto: String)

    fun markAsUpdated(codProduto: String)
}
