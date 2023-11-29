package br.symbiosys.solucoes.cronospharma.entidades.ems

import br.symbiosys.solucoes.cronospharma.entidades.diretorios.Diretorio
import org.apache.commons.lang3.StringUtils
import java.io.File
import java.time.LocalDateTime

class RetornoNotaEMS {


    // Cabecalho
    var cnpjFarmacia: String? = null
    var numeroNotaFiscal: String? = null
    var numeroSerieNotaFiscal: String? = null
    var numeroPedidoIndustria: String? = null
    var numeroPedidoOL: String? = null
    var dataFaturamento: LocalDateTime? = null

    // Itens
    var itens: List<ItemRetornoNotaEMS> = mutableListOf()


    fun gerarRetorno(cnpj: String, diretorio: Diretorio): File {
        val conteudo = StringBuilder()
        val dataGeracao = LocalDateTime.now().toString()

        //header
        conteudo.append("0")
        conteudo.append(StringUtils.rightPad("Nota Fiscal OL", 15, " "))
        conteudo.append(StringUtils.leftPad(cnpj, 15, "0"))
        conteudo.append("${dataGeracao.substring(8,10)}${dataGeracao.substring(5,7)}${dataGeracao.substring(0,4)}")
        conteudo.append("${dataGeracao.substring(11,13)}${dataGeracao.substring(14,16)}${dataGeracao.substring(17,19)}00")
        conteudo.append("\n")


        //cabecalho
        conteudo.append("1")
        conteudo.append(StringUtils.leftPad(cnpjFarmacia, 15, "0"))
        conteudo.append(StringUtils.leftPad(cnpj, 15, "0"))
        conteudo.append(StringUtils.leftPad(numeroNotaFiscal, 8, "0"))
        conteudo.append(StringUtils.leftPad(numeroSerieNotaFiscal, 3, "0"))
        conteudo.append(StringUtils.rightPad(numeroPedidoIndustria, 12, " "))
        conteudo.append("${dataFaturamento.toString().substring(8,10)}${dataFaturamento.toString().substring(5,7)}${dataFaturamento.toString().substring(0,4)}")
        conteudo.append("${dataFaturamento.toString().substring(11,13)}${dataFaturamento.toString().substring(14,16)}${dataFaturamento.toString().substring(17,19)}00")
        conteudo.append(StringUtils.leftPad(numeroPedidoOL, 12, " "))
        conteudo.append("\n")

        // itens
        itens.forEach {
            conteudo.append("2")
            conteudo.append(StringUtils.leftPad(cnpj, 15, "0"))
            conteudo.append(StringUtils.leftPad(numeroNotaFiscal, 8, "0"))
            conteudo.append(StringUtils.leftPad(numeroSerieNotaFiscal, 3, "0"))
            conteudo.append(StringUtils.leftPad(it.codigoEANProduto, 14, "0"))
            conteudo.append(StringUtils.rightPad(numeroPedidoIndustria, 12, " "))
            conteudo.append("0")
            conteudo.append(StringUtils.leftPad(it.quantidadeAtendida.toString().replace(".", ""), 5, "0"))
            conteudo.append(StringUtils.leftPad(it.descontoAplicado.toString().replace(".", ""), 5, "0"))
            conteudo.append("30\n") // prazo medio
        }

        // rodape
        conteudo.append("3")
        conteudo.append(StringUtils.leftPad(cnpj, 15, "0"))
        conteudo.append(StringUtils.leftPad(numeroNotaFiscal, 8, "0"))
        conteudo.append(StringUtils.leftPad(numeroPedidoIndustria, 12, " "))
        conteudo.append(StringUtils.leftPad(itens.size.toString(), 5, "0"))
        conteudo.append(StringUtils.leftPad(itens.size.toString(), 5, "0"))

        val nomeArquivo = "NOTEMS_${cnpj}_${dataGeracao.substring(0,10).replace("-","")}${dataGeracao.substring(11,19).replace(":","")}.txt"
        val file = File(diretorio.diretorioRetornoLocal + nomeArquivo)
        file.writeText(conteudo.toString())
        return file

    }

}