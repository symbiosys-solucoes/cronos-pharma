package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.diretorios

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.TipoIntegracao

class Diretorio
    (
    val tipoIntegracao: TipoIntegracao,
    val usaFTP: Boolean,
    val ativo: Boolean,
    val url: String,
    val login: String,
    val senha: String,
    val diretorioPedidoFTP: String?,
    val diretorioRetornoFTP: String?,
    val diretorioPedidoLocal: String?,
    val diretorioRetornoLocal: String?,
    val diretorioImportadosLocal: String?
            ) {
    override fun toString(): String {
        return "Diretorio(tipoIntegracao=$tipoIntegracao, usaFTP=$usaFTP, ativo=$ativo, url=$url, login=$login, senha=$senha, diretorioPedidoFTP=$diretorioPedidoFTP, diretorioRetornoFTP=$diretorioRetornoFTP, diretorioPedidoLocal=$diretorioPedidoLocal, diretorioRetornoLocal=$diretorioRetornoLocal, diretorioImportadosLocal=$diretorioImportadosLocal)"
    }
}