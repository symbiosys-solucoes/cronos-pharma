package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.medquimica

import java.time.LocalDateTime

class ItemNotaFiscal {

    val tipoRegistro = "2"
    var codigoBarras: String? = null
    var numeroPedido: String? = null
    var quantidadeAtendida: Int = 0
    var descontoAplicado: String? = "0.0"
    var prazoConcedido: String? = null
    var quantidadeNaoAtendida : Int = 0
    var motivo: String? = null
    var numeroNotaFiscal: String? = null
    var serieNotaFiscal: String? = null
    var dataEmissaoNotaFiscal: LocalDateTime? = null
    var chaveNotaFiscal: String? = null
    var valorLiquidoNotaFiscal: String? = null
    var valorSubstituicaoTributaria : String? = null



}
