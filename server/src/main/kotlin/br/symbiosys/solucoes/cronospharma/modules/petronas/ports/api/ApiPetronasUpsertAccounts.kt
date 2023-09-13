package br.symbiosys.solucoes.cronospharma.modules.petronas.ports.api

import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.Accounts
import br.symbiosys.solucoes.cronospharma.modules.petronas.models.response.UpsertResponse
import org.springframework.http.ResponseEntity


interface ApiPetronasUpsertAccounts {
    fun upsertAccounts(accounts: List<Accounts>): ResponseEntity<List<UpsertResponse>>

    fun upsertAccountsDeliveryAddress(accounts: List<Accounts>): ResponseEntity<List<UpsertResponse>>
}