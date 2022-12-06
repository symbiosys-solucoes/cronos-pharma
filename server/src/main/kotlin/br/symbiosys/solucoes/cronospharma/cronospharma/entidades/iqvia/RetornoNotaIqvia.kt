package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.iqvia

import java.time.LocalDate
import java.time.LocalDateTime

class RetornoNotaIqvia {
    // Cabecalho
    val dataGeracaoArquivo = LocalDateTime.now()
    var cnpjDistribuidor: String? = null
    var numeroPedidoOl: String? = null
    var numeroPedidoCliente: String? = null

    // dados nota fiscal
    var dataSaidaMercadoria: LocalDateTime? = null
    var dataEmissaoNF: LocalDate? = null
    var cnpjCliente: String? = null
    var numeroNotaFiscal: String? = null
    var serieNF: String? = null
    var vencimentoFatura: LocalDate? = null

    // totais nota fiscal
    var valorFrete: Double = 0.0
    var valorSeguro: Double = 0.0
    var valorDespesas: Double = 0.0
    var valorTotalProdutos: Double = 0.0
    var valorTotalNF: Double = 0.0
    var valorIPI: Double = 0.0

    //Impostos da nota fical
    var baseCalculoIcms: Double = 0.0
    var valorIcms: Double = 0.0
    var baseCalculoSubstituicaoTributaria: Double = 0.0
    var valorIcmsSubstituicaoTributaria: Double = 0.0

    // Itens da NF
    var itens: List<ItemRetornoNotaIqvia> = mutableListOf()

    // fim do arquivo
    var quatidaItensNotaFiscal: Int = 0
}