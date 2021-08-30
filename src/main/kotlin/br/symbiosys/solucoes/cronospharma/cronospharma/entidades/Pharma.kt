package br.symbiosys.solucoes.cronospharma.cronospharma.entidades

class Pharma
    (
    val tipoIntegracao: TipoIntegracao,
    val usaFTP: Boolean,
    val ativo: Boolean,
    val url: String?,
    val login: String?,
    val senha: String?,
    val diretorioPedidoFTP: String?,
    val diretorioRetornoFTP: String?,
    val diretorioPedidoLocal: String?,
    val diretorioRetornoLocal: String?,
    val diretorioImportadosLocal: String?
            ) {
}