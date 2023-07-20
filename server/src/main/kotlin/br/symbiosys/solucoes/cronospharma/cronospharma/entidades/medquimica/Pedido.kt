package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.medquimica

import java.time.LocalDate

class Pedido {

    val tipoRegistro = "1"
    var cnpjFarmacia: String? = null
    var numeroPedidoMedquimica: String? = null
    var dataPedido: LocalDate? = null
    val vago = "000"
    var tipoCompra = 'E'
    val tipoRetorno = "C"
    var linhaProduto: String? = null
    val compraIntegral = '0'
    var cnpjDistribuidor: String? = null
    var numeroOS: String? = null
    var observacao : String? = null
    var codigoRepresentante : String? = null
    var codigoCampanha: String? = null
    var faturamentoIntegral : String? = null
    var prazo : String? = null
    var codigoPrazo : String? = null
    var items : MutableList<ItemPedido> = mutableListOf()

}