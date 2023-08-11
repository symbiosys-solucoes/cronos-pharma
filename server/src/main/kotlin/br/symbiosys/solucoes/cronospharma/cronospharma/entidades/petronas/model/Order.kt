package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate
import java.time.LocalDateTime

class Order {

    @JsonProperty("SFAOrderNumber__c")
    var orderNumberSfa: String? = null

    @JsonProperty("ERPOrderNumber2__c")
    var orderNumberErp: String? = null

    @JsonProperty("ebMobile__OrderSource__c")
    var orderSource: String? = null

    @JsonProperty("Type")
    var type: String? = null

    @JsonProperty("DTCode__c")
    var dtCode: String? = null

    @JsonProperty("AccountNumber__c")
    var accountNumber: String? = null

    @JsonProperty("Status")
    var status: String? = null

    @JsonProperty("ebMobile__OrderDate__c")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-ddTHH:mm:ss")
    var orderDate: LocalDate? = null

    @JsonProperty("ebMobile__TotalQuantity__c")
    var totalQuantity: Double = 0.0

    @JsonProperty("ebMobile__ConfirmedQuatntity__c")
    var confirmedQuantity: Double = 0.0

    @JsonProperty("OrderTotalVolume__c")
    var orderTotalVolume: Double = 0.0

    @JsonProperty("ebMobile__NetAmount__c")
    var netAmount: Double = 0.0

    @JsonProperty("ebMobile__TotalAmount__c")
    var totalAmount: Double = 0.0

    @JsonProperty("PaymentMethod__c")
    var paymentMethod: Double = 0.0

    @JsonProperty("ebMobile__PaymentKeyTerms__c")
    var paymentKeyTerms: String? = null

    @JsonProperty("Destination__c")
    var destination: String? = null

    @JsonProperty("DeliveryType__c")
    var deliveryType: String? = null

    @JsonProperty("ExpectedDeliveryDate__c")
    var expectedDeliveryDate: LocalDateTime? = null

    @JsonProperty("ebMobile__DriverMessage__c")
    var driverMessage: String? = null

    @JsonProperty("PoNumber")
    var customerOrderNumber: String? = null

    @JsonProperty("UserCode__c")
    var userCode: String? = null

    @JsonProperty("TakenBy__c")
    var takenBy: String? = null

    @JsonProperty("ebMobile__IsActive__c")
    var active: Boolean = true

    @JsonProperty("ShipToAccountNumber__c")
    var shipToCode: Boolean = true

}