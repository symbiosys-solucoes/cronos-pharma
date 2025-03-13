package br.symbiosys.solucoes.cronospharma.modules.petronas.models.request

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty

open class Order {

    @JsonProperty("Id")
    var salesForceId: String? = null

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
    open var orderDate: String? = null

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
    var paymentMethod: String? = null

    @JsonProperty("ebMobile__PaymentKeyTerms__c")
    var paymentKeyTerms: String? = null

    @JsonProperty("Destination__c")
    var destination: String? = null

    @JsonProperty("DeliveryType__c")
    var deliveryType: String? = null

    @JsonProperty("ExpectedDeliveryDate__c")
    open var expectedDeliveryDate: String? = null

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
    var shipToAccountNumber: String? = null

    @JsonIgnore
    var idCronos: Int? = null

}

class OrderRequest : Order() {
    @JsonProperty("ExpectedDeliveryDate__c")
    override var expectedDeliveryDate: String? = null

    @JsonProperty("ebMobile__OrderDate__c")
    override var orderDate: String? = null

    companion object{
        fun from(order: Order): OrderRequest {
            return OrderRequest().apply {
                salesForceId = order.salesForceId
                orderNumberSfa = order.orderNumberSfa
                orderNumberErp = order.orderNumberErp
                orderSource = order.orderSource
                type = order.type
                dtCode = order.dtCode
                accountNumber = order.accountNumber
                status = order.status
                orderDate = order.orderDate
                totalQuantity = order.totalQuantity
                confirmedQuantity = order.confirmedQuantity
                orderTotalVolume = order.orderTotalVolume
                netAmount = order.netAmount
                totalAmount = order.totalAmount
                paymentMethod = order.paymentMethod
                paymentKeyTerms = order.paymentKeyTerms
                destination = order.destination
                deliveryType = order.deliveryType
                expectedDeliveryDate = order.expectedDeliveryDate
                driverMessage = order.driverMessage
                customerOrderNumber = customerOrderNumber.toString() + "0+0000"
                userCode = order.userCode
                takenBy = order.takenBy
                active = order.active
                shipToAccountNumber = order.shipToAccountNumber
            }
        }
    }
}