package br.symbiosys.solucoes.cronospharma.modules.petronas.ports.api

import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.AccountAR
import br.symbiosys.solucoes.cronospharma.modules.petronas.models.response.UpsertResponse
import org.springframework.http.ResponseEntity

interface ApiPetronasUpsertAccountAR {
    fun upsertAccountAr(accounts: List<AccountAR>): ResponseEntity<List<UpsertResponse>>
}
