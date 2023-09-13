package br.symbiosys.solucoes.cronospharma.modules.petronas.models.request

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

class OrderItem {

    @JsonProperty("Id")
    var sfaOrderItemId: String? = null

    @JsonProperty("SFAOrderNumber__c")
    var orderNumberSfa: String? = null

    @JsonProperty("ERPOrderNumber2__c")
    var orderNumberErp: String? = null

    @JsonProperty("SFAOrderItemNumber__c")
    var orderItemNumberSfa: String? = null

    @JsonProperty("ERPOrderItemNumber__c")
    var orderItemNumberErp: String? = null

    @JsonProperty("ebMobile__ErpProductCode__c")
    var productCodeErp: String? = null

    @JsonProperty("ebMobile__OrderItemStatus__c")
    var orderItemStatus: String? = null

    @JsonProperty("ebMobile__OrderQuantity__c")
    var orderQuantity: Double = 0.0

    @JsonProperty("ebMobile__ConfirmedQuantity__c")
    var confirmedQuantity: Double = 0.0

    @JsonProperty("ebMobile__ConfirmedDate__c")
    @JsonFormat(pattern = "yyyy-MM-dd")
    var confirmedDate: LocalDate? = null

    @JsonProperty("TotalVol__c")
    var totalVolume: Double = 0.0

    @JsonProperty("UnitPrice")
    var unitPrice: Double = 0.0

    @JsonProperty("FinalUnitPricePerPack__c")
    var finalUnitPrice: Double = 0.0

    @JsonProperty("ebMobile__LineNetAmount__c")
    var lineNetAmount: Double = 0.0

    @JsonProperty("ebMobile__LineAmount__c")
    var lineAmount: Double = 0.0

    @JsonProperty("Discount_Pcentage__c")
    var discountPercentage: Double = 0.0

    @JsonProperty("ebMobile__OrderDate__c")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    var orderDate: LocalDate? = null

    @JsonProperty("ebMobile__ItemSequence__c")
    var itemSequence: Int = 0

    @JsonProperty("Manufacturer__c")
    var manufacturer: String? = null

    @JsonProperty("COGS__c")
    var totalCost: Double = 0.0

    @JsonProperty("ebMobile__IsActive__c")
    var active: Boolean = true

    @JsonProperty("DTCode__c")
    var dtCode: String? = null

}