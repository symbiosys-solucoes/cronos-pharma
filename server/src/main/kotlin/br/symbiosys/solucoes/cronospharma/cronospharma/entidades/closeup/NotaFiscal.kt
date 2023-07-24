package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.closeup

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.diretorios.Diretorio
import org.apache.commons.lang3.StringUtils
import java.io.File
import java.lang.StringBuilder
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class NotaFiscal {

    val tipoRegistro: String = "1"
    var cnpjFarmacia: String? = null
    var numeroPedidoMedquimica: String? = null
    var dataFaturamento: LocalDateTime? = null
    var condicaoPagamento : String = "0"
    var numeroPedido: String? = null
    var situacaoFechamento = "00"
    var cnpjDistribuidor: String? = null
    var numeroNotaFiscal: String? = null
    var serieNF: String? = null
    var vencimentoFatura: LocalDate? = null
    var valorFrete: Double = 0.0
    var valorSeguro: Double = 0.0
    var valorDespesas: Double = 0.0
    var valorTotalProdutos: Double = 0.0
    var valorTotalNF: Double = 0.0
    var valorIPI: Double = 0.0
    var baseCalculoIcms: Double = 0.0
    var valorIcms: Double = 0.0
    var baseCalculoSubstituicaoTributaria: Double = 0.0
    var valorIcmsSubstituicaoTributaria: Double = 0.0
    var dataEmissaoNotaFiscal: LocalDateTime? = null
    var chaveNotaFiscal: String? = null

    var items : MutableList<ItemNotaFiscal> = mutableListOf()

    fun gerarRetorno(cnpj: String, diretorio: Diretorio): File {
        val format = DateTimeFormatter.ofPattern("ddMMyyyy")
        var conteudo = StringBuilder()
        val dataGeracao = LocalDateTime.now().toString()

        val dia = dataEmissaoNotaFiscal.toString().substring(8,10)
        val mes = dataEmissaoNotaFiscal.toString().substring(5,7)
        val ano = dataEmissaoNotaFiscal.toString().substring(0,4)
        val hora = dataEmissaoNotaFiscal.toString().substring(11,13)
        val minuto = dataEmissaoNotaFiscal.toString().substring(14,16)
        val segundos = dataEmissaoNotaFiscal.toString().substring(17,19) + "00"

        //cabecalho
        conteudo.append(tipoRegistro)
        conteudo.append(cnpjFarmacia)
        conteudo.append(StringUtils.leftPad(numeroPedidoMedquimica, 20, " "))
        conteudo.append(dia + mes + ano)
        conteudo.append(condicaoPagamento)
        conteudo.append(hora + minuto + segundos)
        conteudo.append(StringUtils.leftPad(numeroPedido, 12, " "))
        conteudo.append("00")
        conteudo.append(vencimentoFatura?.format(format) ?: dia + mes + ano)
        conteudo.append(cnpj)
        conteudo.append("\n")

        // itens da nota fiscal
        items.forEach {
            conteudo.append(it.tipoRegistro)
            conteudo.append(StringUtils.leftPad(it.codigoBarras, 13, " "))
            conteudo.append(StringUtils.leftPad(numeroPedidoMedquimica, 20, " "))
            conteudo.append(StringUtils.leftPad(it.quantidadeAtendida?.toInt().toString(),5,"0"))
            conteudo.append(StringUtils.leftPad(it.descontoAplicado.toString().replace(".", ""),5,"0"))
            conteudo.append("   ")
            conteudo.append(StringUtils.leftPad(numeroPedido, 62, " "))
            conteudo.append(StringUtils.leftPad(serieNF, 62, " "))
            conteudo.append(dia + mes + ano)
            conteudo.append(chaveNotaFiscal)
            conteudo.append(it.valorLiquidoNotaFiscal)
            conteudo.append(it.valorSubstituicaoTributaria)
            conteudo.append("\n")
        }

        // fim do arquivo
        conteudo.append("3")
        conteudo.append(StringUtils.leftPad(numeroPedidoMedquimica, 20, " "))
        conteudo.append(StringUtils.leftPad(items.size.plus(2).toString(), 5, " "))
        conteudo.append("00000")


        val nomeArquivo = "ESPELHO${numeroPedidoMedquimica}_XX_${cnpjFarmacia}.txt"
        val file = File(diretorio.diretorioRetornoLocal + nomeArquivo)

        file.writeText(conteudo.toString())
        return file
    }
}