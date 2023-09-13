package br.symbiosys.solucoes.cronospharma.modules.petronas.ports.repositories

import java.time.LocalDateTime

class ItemPedidoPalmPetronas {

    var idItemPedido: Int? = null

    var idPedidoPalmPetronas: Int = 0

    var sequencialItem: Int = 1

    var codigoProdutoArquivo: String? = null

    var idProduto: Int = 0

    var codigoProduto: String? = null

    var quantidadeSolicitada: Double = 0.0

    var quantidadeConfirmada: Double = 0.0

    var idPrecoTabela: Int = 0

    var precoUnitario: Double = 0.0

    var percentualDesconto: Double = 0.0

    var percentualComissao: Double = 0.0

    var situacaoItem: String = "P"

    var logImportacao: String? = null

    var codigoRetornoItem: String? = null

    var descricaoRetornoItem: String? = null

    val idUsuario = "SYM"

    var dataOperacao = LocalDateTime.now()


}
