package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.api

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.request.*
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.response.UpsertResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PutMapping

@FeignClient(name = "petronas-upsert-account-ar", url = "\${app.petronas.base.url}")
interface ApiPetronasUpsertAccountAR {

    @PutMapping(value = ["/services/apexrest/ebMobile/erp2?functionName=UpsertAccountARs"])
    fun upsertAccountAr(accounts: List<AccountAR>): ResponseEntity<List<UpsertResponse>>


}