package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.api

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.request.Accounts
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.response.UpsertResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PutMapping

@FeignClient(name = "petronas-upsert-accounts", url = "\${app.petronas.base.url}")
interface ApiPetronasUpsertAccounts {

    @PutMapping(value = ["/services/apexrest/ebMobile/erp2?functionName=UpsertAccounts"])
    fun upsertAccounts(accounts: List<Accounts>): ResponseEntity<List<UpsertResponse>>

    @PutMapping(value = ["/services/apexrest/ebMobile/erp2?functionName=UpsertShiptos"])
    fun upsertAccountsDeliveryAddress(accounts: List<Accounts>): ResponseEntity<List<UpsertResponse>>


}