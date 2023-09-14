package br.symbiosys.solucoes.cronospharma.entidades.closeup

import java.math.BigDecimal

class ItemPedido {

    val tipoRegistro = '2'
    var numeroPedidoFornecedor: String? = null
    var codigoBarras: String? = null
    var quantidade: Int = 0
    var condicacaoComercial: String? = null
    var desconto: String? = null
    var prazo: String? = null
    var condicaoDesconto: String? = null
    var condicaoPrazo : String? = null
    var codigoPrazo : String? = null
    var codigoOferta : String? = null
    var preco : BigDecimal = BigDecimal("0.0")


}
