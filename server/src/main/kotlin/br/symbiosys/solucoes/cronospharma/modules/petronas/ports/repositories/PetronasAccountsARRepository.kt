package br.symbiosys.solucoes.cronospharma.modules.petronas.ports.repositories

import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.AccountAR


interface PetronasAccountsARRepository {

    fun findAll(): List<AccountAR>

    fun markAsCreated(salesId: String, idCpr: Int)

    fun markAsUpdated(salesId: String, idCpr: Int)

}
