package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model

import com.fasterxml.jackson.annotation.JsonProperty

class PromotionProducts {
    @JsonProperty("ID")
    var salesForceId: String? = null
    @JsonProperty("DMSPromotionProductID__c")
    var erpPromotionProductId: String? = null
    @JsonProperty("DMSPromotionSchemeID__c")
    var promotionSchemeId: String? = null
    @JsonProperty("PromotionScheme__c")
    var promotionScheme: String? = null
    @JsonProperty("PackType__c")
    var packType: String? = null
    @JsonProperty("ProductCode__c")
    var productCode: String? = null
    @JsonProperty("IsGift__c")
    var isGift: Boolean? = false
    @JsonProperty("PromotionUnitPrice__c")
    var promotionUnitPrice: Double = 0.0
    @JsonProperty("ThresholdValue__c")
    var thresholdValue: Double = 0.0
    @JsonProperty("DTQuota__c")
    var dtQuota: Double = 0.0
    @JsonProperty("AccountQuota__c")
    var accountQuota: Double = 0.0
    @JsonProperty("Manufacturer__c")
    var manufacturer: String? = null
    @JsonProperty("IsActive__c")
    var isActive: Boolean = true

}