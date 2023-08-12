package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.request

import com.fasterxml.jackson.annotation.JsonProperty

class Invoice {
    @JsonProperty("ebMobile__OrderNumber__c")
    lateinit var ebMobileOrderNumber: String

    @JsonProperty("DTCode__c")
    lateinit var dtCode: String

    @JsonProperty("Invoice_Document_Number__c")
    lateinit var invoiceDocumentNumber: String

    @JsonProperty("InvoiceSerialNumber__c")
    lateinit var invoiceSerialNumber: String

    @JsonProperty("ebMobile__TotalQuantity__c")
    var ebMobileTotalQuantity: Int = 0

    @JsonProperty("ebMobile__BillingDate__c")
    lateinit var ebMobileBillingDate: String

    @JsonProperty("ebMobile__PaymentTerms__c")
    lateinit var ebMobilePaymentTerms: String

    @JsonProperty("ebMobile__AccountNumber__c")
    lateinit var ebMobileAccountNumber: String

    @JsonProperty("ebMobile__DeliveryNumber__c")
    lateinit var ebMobileDeliveryNumber: String

    @JsonProperty("ebMobile__InvoiceType__c")
    lateinit var ebMobileInvoiceType: String

    @JsonProperty("ebMobile__SalesOrg__c")
    lateinit var ebMobileSalesOrg: String

    @JsonProperty("ebMobile__Status__c")
    lateinit var ebMobileStatus: String

    @JsonProperty("ebMobile__NetValue__c")
    var ebMobileNetValue: Double = 0.0
}