package br.symbiosys.solucoes.cronospharma.modules.petronas.models.request

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
class TokenResponse {

     val type = "Bearer"
     var token: String? = null
     var error: String? = null
}