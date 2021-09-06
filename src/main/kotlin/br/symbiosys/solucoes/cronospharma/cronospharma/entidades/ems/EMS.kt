package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.ems

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.Integrador
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.TipoIntegracao
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos.ItemPedidoPalm
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos.PedidoPalm
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class EMS(
    val tipoIntegracao: TipoIntegracao = TipoIntegracao.EMS,
    val codigoCliente: String?,
    val numeroPedido: String?,
    val dataPedido: LocalDate?,
    val tipoCompra: String? = null,
    val tipoRetorno: String? = null,
    val apontadorCondicaoComercial: String? = null,
    val numeroPedidoCliente: String? = null,
    val prazo: String? = null,
    val codigoRepresentante: String? = null,
    val produtos: List<ItemEMS>
        ): Integrador{

    override fun toPedidoPalm(): PedidoPalm {

        return PedidoPalm(
            Origem = tipoIntegracao.name,
            CnpjCpfCliFor = codigoCliente,
            NumPedidoPalm = numeroPedido ?: "",
            DataPedido = LocalDateTime.of(dataPedido, LocalTime.now()),
            NumPedidoPalmAux = numeroPedidoCliente,
            itens = produtos.map { ItemPedidoPalm(
                CodProdutoArq = it.codigoProduto,
                Qtd = it.quantidade,
                PercDescontoItem = it.desconto,
                Item = produtos.indexOf(it)
            ) }

        )
    }
}
