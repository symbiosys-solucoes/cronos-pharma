package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos

import java.math.BigDecimal
import java.time.LocalDateTime


class ItemPedidoPalm(

    val IdPedidoPalm: Long?,

    val Item: Int?,

    val IdEmpresa:Long = 1,

    val CodProdutoArq: String?,

    val IdProduto: Int?,

    val CodProduto: String?,

    val Qtd: Double?,

    val QtdConfirmada: Double?,

    val IdPrecoTabela: String?,

    val PrecoUnit: BigDecimal?,

    val PercDescontoItem: Double?,

    val PercComissaoItem: Double?,

    val SituacaoItemPedido: String?,

    val LogImportacao: String?,

    val CodRetornoItem: String?,

    val DscRetornoItem: String?,

    val IdUsuario:String = "SYM",

    val DataOperacao: LocalDateTime = LocalDateTime.now(),

    ) {
    var IdItemPedidoPalm: Int? = null
}
