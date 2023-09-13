package br.symbiosys.solucoes.cronospharma.modules.petronas.ports.api

import br.symbiosys.solucoes.cronospharma.modules.petronas.models.response.AuthResponse
import org.springframework.http.ResponseEntity

interface ApiPetronasAuth {
    fun getAccessToken(): ResponseEntity<AuthResponse>

}


