package br.symbiosys.solucoes.cronospharma.modules.petronas.ports.repositories

import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.Accounts


interface PetronasAccountsRepository {
    fun findAll(full: Boolean = false): List<Accounts>

    fun markAsCreated(salesId: String, accountNumber: String)

    fun markAsUpdated(salesId: String, accountNumber: String)
}
