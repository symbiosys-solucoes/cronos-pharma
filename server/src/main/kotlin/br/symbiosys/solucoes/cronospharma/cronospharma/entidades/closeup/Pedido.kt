package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.closeup

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.Integrador
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos.ItemPedidoPalm
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos.PedidoPalm
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.diretorios.Diretorio
import org.apache.commons.lang3.StringUtils
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class Pedido: Integrador {

    val tipoRegistro = "1"
    var cnpjFarmacia: String? = null
    var numeroPedidoMedquimica: String? = null
    var dataPedido: LocalDate? = null
    val vago = "000"
    var tipoCompra = 'E'
    val tipoRetorno = "C"
    var linhaProduto: String? = null
    val compraIntegral = '0'
    var cnpjDistribuidor: String? = null
    var numeroOS: String? = null
    var observacao : String? = null
    var codigoRepresentante : String? = null
    var codigoCampanha: String? = null
    var faturamentoIntegral : String? = null
    var prazo : String? = null
    var condicaoPrazo : String? = null
    var codigoPrazo : String? = null
    var items : MutableList<ItemPedido> = mutableListOf()

    override fun toPedidoPalm(): PedidoPalm {
        return PedidoPalm(
            Origem = "CLOSEUP",
            NumPedidoPalm = numeroPedidoMedquimica ?: "",
            CnpjCpfCliFor = cnpjFarmacia,
            DataPedido = LocalDateTime.of(dataPedido, LocalTime.now()),
            NumPedidoPalmAux = numeroOS,
            itens = items.map { ItemPedidoPalm(
                CodProdutoArq = it.codigoBarras ?: "",
                Qtd = it.quantidade.toDouble(),
                PercDescontoItem = it.desconto?.toDouble(),
                Item = items.indexOf(it) + 1
            ) }
        )
    }

    override fun gerarRetorno(cnpj: String, pedidoPalm: PedidoPalm, diretorio: Diretorio): File {

        val conteudo: StringBuilder = java.lang.StringBuilder()
        val dia = pedidoPalm.DataPedido.toString().substring(8,10)
        val mes = pedidoPalm.DataPedido.toString().substring(5,7)
        val ano = pedidoPalm.DataPedido.toString().substring(0,4)
        val hora = pedidoPalm.DataPedido.toString().substring(11,13)
        val minuto = pedidoPalm.DataPedido.toString().substring(14,16)
        val segundos = pedidoPalm.DataPedido.toString().substring(17,19) + "00"

        //cabecalho

        conteudo.append("1")
        conteudo.append("${pedidoPalm.CnpjCpfCliFor}")
        conteudo.append(StringUtils.leftPad(pedidoPalm.NumPedidoPalm, 20, " "))
        conteudo.append(dia + mes + ano)
        conteudo.append("0")
        conteudo.append(hora + minuto + segundos)
        conteudo.append(StringUtils.leftPad(pedidoPalm.NumPedidoPalm, 12, " "))
        conteudo.append("00")
        conteudo.append(LocalDate.now().plusDays(14).format(DateTimeFormatter.ofPattern("ddMMyyyy")))
        conteudo.append(cnpj)
        conteudo.append("\n")

        //itens

        pedidoPalm.itens.forEach {
            val quantidadeNaoAtendida = it.Qtd.minus(it.QtdConfirmada!!).toInt()
            conteudo.append("2")
            conteudo.append(StringUtils.leftPad(it.CodProdutoArq, 13, " "))
            conteudo.append(StringUtils.leftPad(pedidoPalm.NumPedidoPalm, 20, " "))
            conteudo.append(StringUtils.leftPad(it.QtdConfirmada.toString(),  5, " "))
            conteudo.append("     ")
            conteudo.append("   ")
            conteudo.append(StringUtils.leftPad(quantidadeNaoAtendida.toString(),  5, " "))
            conteudo.append(StringUtils.rightPad(it.CodRetornoItem,  50, " "))
            conteudo.append("\n")
        }

        //rodape
        var quantidadeAtendidas = 0
        var quantidadeNaoAtendida = 0
        pedidoPalm.itens.forEach {
            quantidadeAtendidas += it.QtdConfirmada!!.toInt()
            quantidadeNaoAtendida += (it.Qtd - it.QtdConfirmada).toInt()
        }
        conteudo.append("3")
        conteudo.append(StringUtils.leftPad(pedidoPalm.NumPedidoPalm, 20, " "))
        conteudo.append(StringUtils.leftPad(items.size.plus(2).toString(), 5, " "))
        conteudo.append(StringUtils.leftPad(quantidadeAtendidas.toString(), 5, " "))
        conteudo.append(StringUtils.leftPad(quantidadeNaoAtendida.toString(), 5, " "))

        //nome do arquivo
        val nomeArquivo = "RETORNO_${pedidoPalm.NumPedidoPalm}_4M_${pedidoPalm.CnpjCpfCliFor}.txt"
        val file = File(diretorio.diretorioRetornoLocal + nomeArquivo)

        file.writeText(conteudo.toString())
        return file

    }


}