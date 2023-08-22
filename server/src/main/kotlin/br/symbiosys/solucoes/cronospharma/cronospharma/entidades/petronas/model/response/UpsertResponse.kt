package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.response

import com.fasterxml.jackson.annotation.JsonProperty

class UpsertResponse {


        @JsonProperty("SFDCId")
        var sfdcId: String? = null

        @JsonProperty("isSuccess")
        var isSuccess: Boolean = false

        @JsonProperty("isCreated")
        var isCreated: Boolean = false

        @JsonProperty("ExternalId")
        var externalId: String? = null

        @JsonProperty("Errors")
        var errors: String? = null


}