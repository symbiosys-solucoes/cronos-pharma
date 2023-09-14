package br.symbiosys.solucoes.cronospharma.modules.petronas.ports.repositories

import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.KeyProductsInventory

interface ProductsInventoryPetronasRepository {
    fun findAll(): MutableList<KeyProductsInventory>
}
