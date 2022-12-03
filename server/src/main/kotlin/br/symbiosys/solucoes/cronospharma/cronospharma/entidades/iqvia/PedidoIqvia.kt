package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.iqvia

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.TipoIntegracao
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos.ItemPedidoPalm
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos.PedidoPalm
import java.time.LocalDateTime

class PedidoIqvia (
    val tipoIntegracao: TipoIntegracao = TipoIntegracao.IQVIA,
    val cnpjCliente: String?,
    val tipoFaturamento: Int?,
    val apontadorPromocao: String?,
    val descontoDistribuidorOuVan: String?,
    val codigoProjeto: String?,
    val numeroPedidoIqvia: String?,
    val cnpjCD: String?,
    val tipoPagamento: String?,
    val codigoPrazoDeterminado: String?,
    val numeroDiasPrazoDeterminado: String?,
    val numeroPedidoPrincipal: String?,
    val numeroPedidoCliente: String?,
    val dataPedido: LocalDateTime?,
    val itensPedidoIqvia: List<ItemPedidoIqvia>,
    val quatidadeItems: Int?,
    )

{
    fun toPedidoPalm(): PedidoPalm {
        return PedidoPalm(
            Origem = "REDEFTB",
            NumPedidoPalm = numeroPedidoIqvia ?: "",
            CnpjCpfCliFor = cnpjCliente,
            DataPedido = dataPedido,
            NumPedidoPalmAux = numeroPedidoCliente,
            itens = itensPedidoIqvia.map { ItemPedidoPalm(
                CodProdutoArq = it.codigoEAN ?: "",
                Qtd = it.quantidade,
                PercDescontoItem = it.descontoItem,
                Item = itensPedidoIqvia.indexOf(it) + 1
            ) }
        )
    }
}