package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.iqvia

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.TipoIntegracao
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