package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.medquimica

import java.time.LocalDate
import java.time.LocalDateTime

class NotaFiscal {

    val tipoRegistro: String = "1"
    var cnpjFarmacia: String? = null
    var numeroPedidoMedquimica: String? = null
    var dataFaturamento: LocalDateTime? = null
    var condicaoPagamento : String? = null
    var numeroPedido: String? = null
    var situacaoFechamento = "00"
    var dataVencimento: LocalDate? = null
    var cnpjDistribuidor: String? = null
    var items : MutableList<ItemNotaFiscal> = mutableListOf()

}