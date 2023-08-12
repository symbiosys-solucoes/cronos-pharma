package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.response

import com.fasterxml.jackson.annotation.JsonProperty

data class AuthResponse
    (
    @JsonProperty("access_token")
    val accessToken: String,
    @JsonProperty("instance_url")
    val instanceUrl: String,

    val id: String,
    @JsonProperty("token_type")
    val tokenType: String,
    @JsonProperty("issued_at")
    val issuedAt: String,
    val signature: String
)