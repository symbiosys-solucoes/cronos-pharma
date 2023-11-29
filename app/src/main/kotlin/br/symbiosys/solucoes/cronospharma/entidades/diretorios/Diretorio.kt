package br.symbiosys.solucoes.cronospharma.entidades.diretorios

import br.symbiosys.solucoes.cronospharma.entidades.TipoIntegracao
import com.fasterxml.jackson.annotation.JsonProperty

class Diretorio
    (
    @JsonProperty("integrador")
    val tipoIntegracao: TipoIntegracao,
    @JsonProperty("utiliza_ftp")
    val usaFTP: Boolean,
    @JsonProperty("ativo")
    val ativo: Boolean,
    val url: String,
    val login: String,
    val senha: String,
    @JsonProperty("caminho_de_pedidos_ftp")
    val diretorioPedidoFTP: String? = null,
    @JsonProperty("caminho_de_retornos_ftp")
    val diretorioRetornoFTP: String? = null,
    @JsonProperty("caminho_de_pedidos_local")
    val diretorioPedidoLocal: String? = null,
    @JsonProperty("caminho_de_retorno_local")
    val diretorioRetornoLocal: String? = null,
    @JsonProperty("caminho_de_pedidos_importados")
    val diretorioImportadosLocal: String? = null,
    @JsonProperty("caminho_de_arquivos_de_estoque_local")
    val diretorioEstoqueLocal: String? = null,
    @JsonProperty("caminho_de_arquivos_de_estoque_ftp")
    val diretorioEstoqueFTP: String? = null,
    @JsonProperty("idProduto")
    val idDiretorio: Long? = null
            ) {
    @JsonProperty("caminho_de_arquivos_de_preco_ftp")
    var diretorioPrecoLocal: String? = null
    @JsonProperty("caminho_de_arquivos_de_preco_ftp")
    var diretorioPrecoFTP: String? = null
    override fun toString(): String {
        return "Diretorio(tipoIntegracao=$tipoIntegracao, usaFTP=$usaFTP, ativo=$ativo, url='$url', login='$login', senha='$senha', diretorioPedidoFTP=$diretorioPedidoFTP, diretorioRetornoFTP=$diretorioRetornoFTP, diretorioPedidoLocal=$diretorioPedidoLocal, diretorioRetornoLocal=$diretorioRetornoLocal, diretorioImportadosLocal=$diretorioImportadosLocal, diretorioEstoqueLocal=$diretorioEstoqueLocal, diretorioEstoqueFTP=$diretorioEstoqueFTP, idDiretorio=$idDiretorio)"
    }
}