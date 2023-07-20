package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.medquimica

import java.time.LocalDate
import java.time.LocalDateTime

class Retorno {

    val tipoRegistro = '1'
    var cnpjFarmacia: String? = null
    var numeroPedidoMedquimica: String? = null
    var dataHoraProcessamento: LocalDateTime? = null
    var condicaoPagamento = '0'
    var numeroPedidoDistribuidor: String? = null
    val situacaoFechamento = "00"
    var dataVencimento: LocalDate? = null
    var cnpjDistribuidor: String? = null

    var items: MutableList<ItemRetorno> = mutableListOf()
}




