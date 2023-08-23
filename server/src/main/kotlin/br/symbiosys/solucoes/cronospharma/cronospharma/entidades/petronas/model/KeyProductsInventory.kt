package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model

import com.fasterxml.jackson.annotation.JsonProperty

class KeyProductsInventory {

    @JsonProperty("ProductCode__c")
    var productCode: String? = null

    @JsonProperty("Inventory__c")
    var inventoryQuantity: Double = 0.0

    @JsonProperty("DTCode__c")
    var dtCode: String? = null

    @JsonProperty("ebMobile__IsActive__c")
    var active: Boolean = true

    @JsonProperty("Manufacture__c")
    var manufacture: String = "PETRONAS"

    @JsonProperty("PriceBook__c")
    var priceBook: String = "GeneralPriceBook"

}
