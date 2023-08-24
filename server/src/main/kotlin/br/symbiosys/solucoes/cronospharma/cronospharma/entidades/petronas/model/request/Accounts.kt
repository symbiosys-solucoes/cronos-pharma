package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.request

import CliFor
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

    @JsonProperty("UserCode__c")
    var userCode: String? = null

    @JsonProperty("ebMobile__CreditLimit__c")
    var creditLimit: Double = 0.0



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

    companion object {
        fun fromCliFor(cliFor: CliFor, codigoDistribuidor: String, salesId: String? = null): Accounts {

            return Accounts().apply {
                accountNumber = cliFor.codCliFor
                salesForceId = salesId
                cnpj = cliFor.cpfcgcCliFor
                dtCode = codigoDistribuidor
                accountName = cliFor.razaoSocial
                fantasyName = cliFor.nomeCliFor
                customerType = "Onboarded"
                bussinessType = if(cliFor.tipoPessoa == "J") "B2B" else "B2C"
                accountSource = "DT ERP"
                zipCode = cliFor.cepCliFor
                phone = cliFor.foneCliFor
                phone1 = cliFor.celCliFor
                webSite = cliFor.webSite
                address1 = cliFor.enderecoCliFor
                address2 = cliFor.numeroLogradouro
                city = cliFor.cidade
                neighborhood = cliFor.bairroCliFor
                state = cliFor.ufCliFor
                userCode = if(!cliFor.codFunc.isNullOrBlank()) cliFor.codFunc else if (codigoDistribuidor == "FAALRN") "99999" else "99998"
                creditLimit = cliFor.limiteCredito ?: 0.0
                paymentMethod = cliFor.portadorPadrao
                paymentCondition = cliFor.condicaoPagamentoPadrao
                active = if (cliFor.inativo == "S") false else true
            }
        }
    }
}