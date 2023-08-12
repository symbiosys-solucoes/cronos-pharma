package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model

import com.fasterxml.jackson.annotation.JsonProperty

class PromotionFilterItem {

        @JsonProperty("ID")
        lateinit var salesForceId: String

        @JsonProperty("DMSPromotionFilterItemID__c")
        lateinit var dmsPromotionFilterItemID: String

        @JsonProperty("FieldLabel__c")
        lateinit var fieldLabel: String

        @JsonProperty("FieldName__c")
        lateinit var fieldName: String

        @JsonProperty("OperatorLabel__c")
        lateinit var operatorLabel: String

        @JsonProperty("OperatorValue__c")
        lateinit var operatorValue: String

        @JsonProperty("Value__c")
        lateinit var value: String

        @JsonProperty("Type__c")
        lateinit var type: String

        @JsonProperty("PromotionScheme__c")
        lateinit var promotionScheme: String

        @JsonProperty("DMSPromotionSchemeID__c")
        lateinit var dmsPromotionSchemeID: String
    }

