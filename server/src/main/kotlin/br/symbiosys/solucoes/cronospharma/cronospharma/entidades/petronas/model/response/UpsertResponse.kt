package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.response

import com.fasterxml.jackson.annotation.JsonProperty

class UpsertResponse {


        @JsonProperty("SFDCId")
        lateinit var sfdcId: String

        @JsonProperty("isSuccess")
        var isSuccess: Boolean = false

        @JsonProperty("isCreated")
        var isCreated: Boolean = false

        @JsonProperty("ExternalId")
        lateinit var externalId: String

        @JsonProperty("Errors")
        lateinit var errors: String



}