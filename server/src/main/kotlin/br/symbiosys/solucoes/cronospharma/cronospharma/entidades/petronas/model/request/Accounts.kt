package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.request

import br.symbiosys.solucoes.cronospharma.cronospharma.sym.model.SymCustomer
import com.fasterxml.jackson.annotation.JsonProperty

class Accounts {

    @JsonProperty("ID")
    var salesForceId: String? = null

    @JsonProperty("CNPJ__c")
    var cnpj: String? = null

    @JsonProperty("AccountNumber")
    var accountNumber: String? = null

    @JsonProperty("DTCode__c")
    var dtCode: String? = null

    @JsonProperty("Name")
    var accountName: String? = null

    @JsonProperty("FantansyName__c")
    var fantasyName: String? = null

    @JsonProperty("CustomerType__c")
    var customerType: String? = null

    @JsonProperty("BusinessType__c")
    var bussinessType: String? = null


    @JsonProperty("AccountSource")
    var accountSource: String? = null

    @JsonProperty("ZipCode__c")
    var zipCode: String? = null

    @JsonProperty("Phone")
    var phone: String? = null

    @JsonProperty("Phone_1__c")
    var phone1: String? = null

    @JsonProperty("WebSite")
    var webSite: String? = null

    @JsonProperty("Address1__c")
    var address1: String? = null

    @JsonProperty("Address2__c")
    var address2: String? = null

    @JsonProperty("City__c")
    var city: String? = null

    @JsonProperty("Neighborhood__c")
    var neighborhood: String? = null

    @JsonProperty("ebMobile__State__c")
    var state: String? = null

    @JsonProperty("PaymentMethod__c")
    var paymentMethod: String? = null

    @JsonProperty("ebMobile__PaymentTerms__c")
    var paymentCondition: String? = null

    @JsonProperty("ebMobile__IsActive__c")
    var active: Boolean = true


    fun toSymCustomer(): SymCustomer {
        return SymCustomer().apply {
            idIntegrador = salesForceId
            cpfCnpj = cnpj
            codigoIntegrador = accountNumber
            razaoSocial = accountName
            nomeFantasia = fantasyName
            tipoIntegrador = accountSource
            dadosAdicionais = "Customer Type: $customerType"
            tipoNegocio = bussinessType
            cep = zipCode
            telefone = phone
            telefone2 = phone1
            website = webSite
            endereco = address1
            endereco2 = address2
            cidade = city
            bairro = neighborhood
            uf = state
            condicaoPagamento = paymentMethod
            metodoPagamento = paymentCondition
            ativo = active
        }

    }
}