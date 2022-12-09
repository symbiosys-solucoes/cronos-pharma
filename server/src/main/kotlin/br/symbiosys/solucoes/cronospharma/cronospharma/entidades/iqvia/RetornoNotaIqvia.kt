package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.iqvia

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.diretorios.Diretorio
import org.apache.commons.lang3.StringUtils
import java.io.File
import java.lang.StringBuilder
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
    var quatidadeItensNotaFiscal: Int = 0

    fun gerarRetorno(cnpj: String, diretorio: Diretorio): File {
        var conteudo = StringBuilder()
        val dataGeracao = LocalDateTime.now().toString()

        //cabecalho
        conteudo.append("1${dataGeracao.substring(8,10)}${dataGeracao.substring(5,7)}${dataGeracao.substring(0,4)}")
        conteudo.append("${dataGeracao.substring(11,13)}${dataGeracao.substring(14,16)}${dataGeracao.substring(17,19)}")
        conteudo.append("$cnpj ${StringUtils.leftPad(numeroPedidoOl, 15, "0")}")
        conteudo.append(StringUtils.leftPad(numeroPedidoCliente, 15, "0"))
        conteudo.append("${StringUtils.leftPad(null, 20, " ")}\n")

        // dados nota fiscal
        conteudo.append("2${dataSaidaMercadoria.toString().substring(8,10)}${dataSaidaMercadoria.toString().substring(5,7)}${dataSaidaMercadoria.toString().substring(0,4)}")
        conteudo.append("${dataSaidaMercadoria.toString().substring(11,13)}${dataSaidaMercadoria.toString().substring(14,16)}${dataSaidaMercadoria.toString().substring(17,19)}")
        conteudo.append("${dataEmissaoNF.toString().substring(8,10)}${dataEmissaoNF.toString().substring(5,7)}${dataEmissaoNF.toString().substring(0,4)}")
        conteudo.append(cnpjCliente)
        conteudo.append(StringUtils.leftPad(numeroNotaFiscal, 6, "0"))
        conteudo.append(StringUtils.leftPad(serieNF, 3, "0"))
        conteudo.append("${vencimentoFatura.toString().substring(8,10)}${vencimentoFatura.toString().substring(5,7)}${vencimentoFatura.toString().substring(0,4)}")
        conteudo.append(StringUtils.leftPad(numeroNotaFiscal, 9, "0"))
        conteudo.append(StringUtils.leftPad(serieNF, 3, "0") + "\n")

        // totais nota fiscal
        conteudo.append("3")
        conteudo.append(StringUtils.leftPad(valorFrete.toString().replace(".",""), 8,"0"))
        conteudo.append(StringUtils.leftPad(valorSeguro.toString().replace(".",""), 8,"0"))
        conteudo.append(StringUtils.leftPad(valorDespesas.toString().replace(".",""), 8,"0"))
        conteudo.append(StringUtils.leftPad(valorTotalProdutos.toString().replace(".",""), 8,"0"))
        conteudo.append(StringUtils.leftPad(valorTotalNF.toString().replace(".",""), 8,"0"))
        conteudo.append(StringUtils.leftPad(valorIPI.toString().replace(".",""), 8,"0") + "\n")

        // impostos nota fiscal
        conteudo.append("4")
        conteudo.append(StringUtils.leftPad(baseCalculoIcms.toString().replace(".",""), 8,"0"))
        conteudo.append(StringUtils.leftPad(valorIcms.toString().replace(".",""), 8,"0"))
        conteudo.append(StringUtils.leftPad(baseCalculoSubstituicaoTributaria.toString().replace(".",""), 8,"0"))
        conteudo.append(StringUtils.leftPad(valorIcmsSubstituicaoTributaria.toString().replace(".",""), 8,"0") + "\n")

        // itens da nota fiscal
        itens.forEach {
            conteudo.append("5")
            conteudo.append(StringUtils.leftPad(it.codigoEAN, 13, " "))
            conteudo.append(StringUtils.leftPad(it.codigoProdutoDistribuidor, 7, " "))
            conteudo.append(StringUtils.leftPad(it.quantidade.toString().replace(".", ""),8,"0"))
            conteudo.append(StringUtils.leftPad(it.tipoEmbalagem, 3 , " "))
            conteudo.append(StringUtils.leftPad(it.preco.toString().replace(".", ""),8,"0"))
            conteudo.append(StringUtils.leftPad(it.descontoComercial.toString().replace(".", ""),4,"0"))
            conteudo.append(StringUtils.leftPad(null,8,"0"))
            conteudo.append(StringUtils.leftPad(it.valorRepasse.toString().replace(".", ""),8,"0"))
            conteudo.append(StringUtils.leftPad(it.repasse.toString().replace(".", ""),4,"0"))
            conteudo.append(StringUtils.leftPad(it.valorUnitario.toString().replace(".", ""),8,"0"))
            conteudo.append(StringUtils.leftPad(it.fracionamento.toString().replace(".", ""),4,"0"))
            conteudo.append(StringUtils.leftPad(null, 4, "0") + "\n")
        }

        // fim do arquivo
        conteudo.append("5")
        conteudo.append(StringUtils.leftPad(itens.size.toString(), 4, "0"))
        conteudo.append(StringUtils.leftPad(null, 75, "0"))


        val nomeArquivo = "NOTGEO_${cnpj}_${numeroPedidoOl}.NOT"
        val file = File(diretorio.diretorioRetornoLocal + nomeArquivo)
        file.writeText(conteudo.toString())
        return file
    }
}