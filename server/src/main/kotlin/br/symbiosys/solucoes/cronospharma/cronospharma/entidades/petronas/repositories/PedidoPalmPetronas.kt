package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.repositories

import java.time.LocalDateTime

class PedidoPalmPetronas {

    var idPedidoPalm: Int? = null

    val origem = "PETRONAS"

    var numeroPedido: String? = null

    var codigoVendedor: String? = null

    var codigoCliente: String? = null

    var cnpjCpfCliente: String? = null

    var dataPedido: LocalDateTime = LocalDateTime.now()

    var percentualComissao = 0.0

    var condicaoPagamento: String? = null

    var codigoPortador: String? = null

    var percentualDesconto = 0.0

    var totalPedido = 0.0

    var dataEntrega: LocalDateTime? = null

    var observacoes: String? = null

    var idExpedicao: Int? = null

    var dataProximaVisita: LocalDateTime? = null

    var situacaoPedido: String? = "P"

    var numeroNF: String? = null

    var logImportacao: String? = null

    var statusRetorno: String? = null

    var codigoRetorno: String? = null

    var descricaoRetorno: String? = null

    var numeroPedidoCronos: String? = null

    var arquivoRetornoPedido: String? = null

    var arquivoRetornoNotaFiscal: String? = null

    val idUsuario = "SYM"

    var dataOperacao = LocalDateTime.now()

    var codigoFilial: String? = null

    var arquivoRetornoSegundaViaPedido: String? = null

    var versaoLayout: String? = null

    var numeroPedidoPalmAux: String? = null

    var itens: MutableList<ItemPedidoPalmPetronas> = mutableListOf()





}