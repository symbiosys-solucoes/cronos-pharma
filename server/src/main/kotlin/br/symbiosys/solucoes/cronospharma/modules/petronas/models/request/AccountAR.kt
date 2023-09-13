package br.symbiosys.solucoes.cronospharma.modules.petronas.models.request

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

class AccountAR {
    @JsonProperty("ID")
    var salesForceId: String? = null

    @JsonProperty("ebMobile__Number__c")
    var accountNumber: String? = null

    @JsonProperty("DTCode__c")
    var dtCode: String? = null

    @JsonProperty("ebMobile__PaymentDate__c")
    var paymentDate: LocalDateTime? = null

    @JsonProperty("ebMobile__InvoiceDate__c")
    var invoiceDate: LocalDateTime? = null

    @JsonProperty("ebMobile__InvoiceDueDate__c")
    var invoiceDueDate: LocalDateTime? = null

    @JsonProperty("ebMobile__InvoiceNumber__c")
    var invoiceNumber: String? = null

    @JsonProperty("ebMobile__InvoiceAmount__c")
    var invoiceAmount: Double = 0.0

    @JsonProperty("ebMobile__OpenAmount__c")
    var invoiceOpenAmount: Double = 0.0

    @JsonProperty("ebMobile__CreditLimit__c")
    var creditLimit: Double = 0.0

    @JsonProperty("ebMobile__Remarks__c")
    var remarks: String? = null
}