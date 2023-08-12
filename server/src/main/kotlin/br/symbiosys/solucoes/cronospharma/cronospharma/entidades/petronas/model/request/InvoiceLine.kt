package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.request

import com.fasterxml.jackson.annotation.JsonProperty

class InvoiceLine {

    @JsonProperty("Invoice_Document_Number__c")
    lateinit var invoiceDocumentNumber: String

    @JsonProperty("DTCode__c")
    lateinit var dtCode: String

    @JsonProperty("Manufacture__c")
    lateinit var manufacture: String

    @JsonProperty("InvoiceSerialNumber__c")
    lateinit var invoiceSerialNumber: String

    @JsonProperty("ebMobile__InvoiceItemNumber__c")
    lateinit var ebMobileInvoiceItemNumber: String

    @JsonProperty("ebMobile__InvoiceQuantity__c")
    var ebMobileInvoiceQuantity: Double = 0.0

    @JsonProperty("UnitPrice__c")
    var unitPrice: Double = 0.0

    @JsonProperty("LineTotalFinal__c")
    var lineTotalFinal: Double = 0.0

    @JsonProperty("ebMobile__LineDiscountAmount__c")
    var ebMobileLineDiscountAmount: Double = 0.0

    @JsonProperty("ebMobile__ProductCode__c")
    lateinit var ebMobileProductCode: String

    @JsonProperty("ebMobile__ReferenceDocument__c")
    lateinit var ebMobileReferenceDocument: String

    @JsonProperty("ebMobile__UsageIndicator__c")
    lateinit var ebMobileUsageIndicator: String

    @JsonProperty("ebMobile__IsActive__c")
    var ebMobileIsActive: Boolean = false
}