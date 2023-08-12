package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.request

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

class Promotion {
    @JsonProperty("ID")
    var salesForceId: String? = null

    @JsonProperty("DMSPromotionSchemeID__c")
    var erpPromotionScheme: String? = null

    @JsonProperty("PromotionTitle__c")
    var promotionTitle: String? = null

    @JsonProperty("PromotionDescription__c")
    var promotionDescription: String? = null

    @JsonProperty("PromotionLevel__c")
    var promotionLevel: String? = null

    @JsonProperty("DistributorName__c")
    var distribuitorName: String? = null

    @JsonProperty("PromotionType__c")
    var promotionType: String? = null

    @JsonProperty("PromotionSubType__c")
    var promotionSubType: String? = null

    @JsonProperty("PromotionCriteriaValue__c")
    var promotionCriteriaValue: Double = 0.0

    @JsonProperty("OfferingValue__c")
    var offeringValue: Double = 0.0

    @JsonProperty("IsFOI__c")
    var isFOI: Boolean = true

    @JsonProperty("StartDate__c")
    var promotionStartDate: LocalDate? = null

    @JsonProperty("EndDate__c")
    var promotionEndDate: LocalDate? = null

    @JsonProperty("IsActive__c")
    var active: Boolean = true

    @JsonProperty("ApproveStatus__c")
    var approveStatus: String? = "Approved"

}