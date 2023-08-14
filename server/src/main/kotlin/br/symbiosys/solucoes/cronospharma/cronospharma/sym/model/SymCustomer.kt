package br.symbiosys.solucoes.cronospharma.cronospharma.sym.model

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "Zsym_clientes")
class SymCustomer {

    @Id
    var id: Long? = null

    var idIntegrador: String? = null

    var codigoIntegrador: String? = null

    var tipoIntegrador: String? = null

    var nomeFantasia: String? = null

    var razaoSocial : String? = null

    var cpfCnpj: String? = null

    var tipoNegocio: String? = null

    var cep: String? = null

    var telefone: String? = null

    var telefone2: String? = null

    var website: String? = null

    var endereco: String? = null

    var endereco2: String? = null

    var cidade: String? = null

    var bairro: String? = null

    var uf: String? = null

    var condicaoPagamento: String? = null

    var metodoPagamento : String? = null

    var ativo: Boolean = true


}