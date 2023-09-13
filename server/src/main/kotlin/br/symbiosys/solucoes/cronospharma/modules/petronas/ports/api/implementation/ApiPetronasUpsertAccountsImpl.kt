package br.symbiosys.solucoes.cronospharma.modules.petronas.ports.api.implementation

import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.Accounts
import br.symbiosys.solucoes.cronospharma.modules.petronas.models.response.UpsertResponse
import br.symbiosys.solucoes.cronospharma.modules.petronas.ports.api.ApiPetronasUpsertAccounts
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PutMapping

@FeignClient(name = "petronas-upsert-accounts", url = "\${app.petronas.base.url}")
interface ApiPetronasUpsertAccountsImpl : ApiPetronasUpsertAccounts {

    @PutMapping(value = ["/services/apexrest/ebMobile/erp2?functionName=UpsertAccounts"])
    override fun upsertAccounts(accounts: List<Accounts>): ResponseEntity<List<UpsertResponse>>

    @PutMapping(value = ["/services/apexrest/ebMobile/erp2?functionName=UpsertShiptos"])
    override fun upsertAccountsDeliveryAddress(accounts: List<Accounts>): ResponseEntity<List<UpsertResponse>>


}

