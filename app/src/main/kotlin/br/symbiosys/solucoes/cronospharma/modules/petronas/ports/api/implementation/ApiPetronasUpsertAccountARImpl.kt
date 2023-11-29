package br.symbiosys.solucoes.cronospharma.modules.petronas.ports.api.implementation

import br.symbiosys.solucoes.cronospharma.modules.petronas.models.response.UpsertResponse
import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.AccountAR
import br.symbiosys.solucoes.cronospharma.modules.petronas.ports.api.ApiPetronasUpsertAccountAR
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PutMapping

@FeignClient(name = "petronas-upsert-account-ar", url = "\${app.petronas.base.url}")
interface ApiPetronasUpsertAccountARImpl : ApiPetronasUpsertAccountAR {

    @PutMapping(value = ["/services/apexrest/ebMobile/erp2?functionName=UpsertAccountARs"])
    override fun upsertAccountAr(accounts: List<AccountAR>): ResponseEntity<List<UpsertResponse>>


}

