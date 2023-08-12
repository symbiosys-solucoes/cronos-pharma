package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.request

import com.fasterxml.jackson.annotation.JsonProperty

class AccountAR {
    @JsonProperty("ID")
    lateinit var salesForceId: String

    @JsonProperty("ebMobile__Number__c")
    lateinit var accountNumber: String

    @JsonProperty("DTCode__c")
    lateinit var dtCode: String

    @JsonProperty("ebMobile__PaymentDate__c")
    lateinit var paymentDate: String

    @JsonProperty("ebMobile__InvoiceDate__c")
    lateinit var invoiceDate: String

    @JsonProperty("ebMobile__InvoiceDueDate__c")
    lateinit var invoiceDueDate: String

    @JsonProperty("ebMobile__InvoiceNumber__c")
    lateinit var invoiceNumber: String

    @JsonProperty("ebMobile__InvoiceAmount__c")
    var invoiceAmount: Double = 0.0

    @JsonProperty("ebMobile__OpenAmount__c")
    var invoiceOpenAmount: Double = 0.0

    @JsonProperty("ebMobile__CreditLimit__c")
    var creditLimit: Double = 0.0

    @JsonProperty("ebMobile__Remarks__c")
    lateinit var remarks: String
}