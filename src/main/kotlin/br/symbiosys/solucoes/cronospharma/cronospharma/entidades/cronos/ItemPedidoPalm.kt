package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos

import java.math.BigDecimal
import java.time.LocalDateTime


class ItemPedidoPalm(

    var IdItemPedidoPalm: Long? = null,

    var IdPedidoPalm: Long?,

    val Item: Int?,

    val IdEmpresa:Long = 1,

    val CodProdutoArq: String?,

    var IdProduto: Int?,

    var CodProduto: String?,

    val Qtd: Double?,

    val QtdConfirmada: Double?,

    var IdPrecoTabela: String?,

    var PrecoUnit: BigDecimal?,

    val PercDescontoItem: Double?,

    val PercComissaoItem: Double?,

    val SituacaoItemPedido: String?,

    val LogImportacao: String?,

    val CodRetornoItem: String?,

    val DscRetornoItem: String?,

    val IdUsuario:String = "SYM",

    val DataOperacao: LocalDateTime = LocalDateTime.now(),

    ) {

}
