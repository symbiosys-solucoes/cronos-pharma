package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.api

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.response.AuthResponse
import feign.Headers
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping

@FeignClient(name = "petronas-auth", url = "\${app.petronas.url}")
interface ApiPetronasAuth {

    @PostMapping(value = ["/oauth2/token?grant_type=\${app.petronas.grant_type}&client_id=\${app.petronas.client_id}&client_secret=\${app.petronas.client_secret}&username=\${app.petronas.username}&password=\${app.petronas.password}"])
    @Headers(value = ["grant_type: \${app.petronas.grant_type}", "client_id: \${app.petronas.client_id}",
        "client_secret: \${app.petronas.client_secret}"])
    fun getAccessToken(): ResponseEntity<AuthResponse>

}