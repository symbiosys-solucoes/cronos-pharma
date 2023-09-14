package br.symbiosys.solucoes.cronospharma.modules.petronas.models.request

import com.fasterxml.jackson.annotation.JsonProperty

class Invoice {
    @JsonProperty("ebMobile__OrderNumber__c")
     var ebMobileOrderNumber: String? = null

    @JsonProperty("DTCode__c")
    var dtCode: String? = null

    @JsonProperty("Invoice_Document_Number__c")
    var invoiceDocumentNumber: String? = null

    @JsonProperty("InvoiceSerialNumber__c")
    var invoiceSerialNumber: String? = null

    @JsonProperty("ebMobile__TotalQuantity__c")
    var ebMobileTotalQuantity: Int? = 0

    @JsonProperty("ebMobile__BillingDate__c")
    var ebMobileBillingDate: String? = null

    @JsonProperty("ebMobile__PaymentTerms__c")
    var ebMobilePaymentTerms: String? = null

    @JsonProperty("ebMobile__AccountNumber__c")
    var ebMobileAccountNumber: String? = null

    @JsonProperty("ebMobile__DeliveryNumber__c")
    var ebMobileDeliveryNumber: String? = null

    @JsonProperty("ebMobile__InvoiceType__c")
    var ebMobileInvoiceType: String? = null

    @JsonProperty("ebMobile__SalesOrg__c")
    var ebMobileSalesOrg: String? = null

    @JsonProperty("ebMobile__Status__c")
    var ebMobileStatus: String? = null

    @JsonProperty("ebMobile__NetValue__c")
    var ebMobileNetValue: Double = 0.0
}