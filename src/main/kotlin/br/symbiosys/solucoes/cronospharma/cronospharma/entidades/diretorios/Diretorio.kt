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
    val diretorioPedidoFTP: String? = null,
    val diretorioRetornoFTP: String? = null,
    val diretorioPedidoLocal: String? = null,
    val diretorioRetornoLocal: String? = null,
    val diretorioImportadosLocal: String? = null,
    val diretorioEstoqueLocal: String? = null,
    val diretorioEstoqueFTP: String? = null,
            ) {
    override fun toString(): String {
        return "Diretorio(tipoIntegracao=$tipoIntegracao, usaFTP=$usaFTP, ativo=$ativo, url=$url, login=$login, senha=$senha, diretorioPedidoFTP=$diretorioPedidoFTP, diretorioRetornoFTP=$diretorioRetornoFTP, diretorioPedidoLocal=$diretorioPedidoLocal, diretorioRetornoLocal=$diretorioRetornoLocal, diretorioImportadosLocal=$diretorioImportadosLocal)"
    }
}