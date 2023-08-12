package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.request

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

class KeyProducts {

    @JsonProperty("ProductCode__c")
    var productCode: String? = null

    @JsonProperty("BasePrice__c")
    var basePrice: String? = null

    @JsonProperty("MaxDicounctPct__c")
    var maxDiscount: Double = 0.0

    @JsonProperty("PromoPrice__c")
    var promotionPrice: Double = 0.0

    @JsonProperty("ebMobile__StartDate__c")
    var promotionStartDate: LocalDate? = null

    @JsonProperty("ebMobile__EndDate__c")
    var promotionEndDate: LocalDate? = null

    @JsonProperty("Inventory__c")
    var inventoryQuantity: Double? = 0.0

    @JsonProperty("ebMobile__IsActive__c")
    var active: Boolean = true

    @JsonProperty("DTCode__c")
    var dtCode: String? = null

    @JsonProperty("Manufacture__c")
    var manufacture: String? = "NON PETRONAS"

    @JsonProperty("PriceBook__c")
    var priceBookName: String? = null

}