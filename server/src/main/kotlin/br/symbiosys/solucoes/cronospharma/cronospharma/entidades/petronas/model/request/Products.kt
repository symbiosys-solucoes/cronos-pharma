package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.request

import com.fasterxml.jackson.annotation.JsonProperty

class Products {

    @JsonProperty("ProductCode")
    var productCode: String? = null

    @JsonProperty("DTCode__c")
    var dtCode: String? = null

    @JsonProperty("Name")
    var name: String? = null

    @JsonProperty("Description")
    var description: String? = null

    @JsonProperty("ebMobile__PackType__c")
    var unid: String? = null

    @JsonProperty("Pack_Vol__c")
    var unidQuantity: Double? = 0.0

    @JsonProperty("ebMobile__Category__c")
    var category: String? = null

    @JsonProperty("ebMobile__Brand__c")
    var brand: String? = null

    @JsonProperty("Manufacture__c")
    var manufacture: String? = "NON PETRONAS"

    @JsonProperty("IsActive")
    var active: Boolean = true

    @JsonProperty("IsGift__c")
    var gift: Boolean = false


}
