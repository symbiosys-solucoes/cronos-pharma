package br.symbiosys.solucoes.cronospharma.entidades.cronos

import java.math.BigDecimal
import java.time.LocalDateTime


class ItemPedidoPalm(

    var IdItemPedidoPalm: Long? = null,

    var IdPedidoPalm: Long? = null,

    val Item: Int? = null,

    val IdEmpresa:Long = 1,

    val CodProdutoArq: String,

    var IdProduto: Int? = null,

    var CodProduto: String? = null,

    val Qtd: Double,

    val QtdConfirmada: Double? = null,

    var IdPrecoTabela: String? = null,

    var PrecoUnit: BigDecimal? = null,

    val PercDescontoItem: Double? = null,

    val PercComissaoItem: Double? = null,

    val SituacaoItemPedido: String? = "P",

    val LogImportacao: String? = null,

    val CodRetornoItem: String? = null,

    val DscRetornoItem: String? = null,

    val IdUsuario:String = "SYM",

    val DataOperacao: LocalDateTime = LocalDateTime.now(),

    )
{
    override fun toString(): String {
        return "ItemPedidoPalm(IdItemPedidoPalm=$IdItemPedidoPalm, IdPedidoPalm=$IdPedidoPalm, Item=$Item, IdEmpresa=$IdEmpresa, CodProdutoArq='$CodProdutoArq', IdProduto=$IdProduto, CodProduto=$CodProduto, Qtd=$Qtd, QtdConfirmada=$QtdConfirmada, IdPrecoTabela=$IdPrecoTabela, PrecoUnit=$PrecoUnit, PercDescontoItem=$PercDescontoItem, PercComissaoItem=$PercComissaoItem, SituacaoItemPedido=$SituacaoItemPedido, LogImportacao=$LogImportacao, CodRetornoItem=$CodRetornoItem, DscRetornoItem=$DscRetornoItem, IdUsuario='$IdUsuario', DataOperacao=$DataOperacao)"
    }
}
