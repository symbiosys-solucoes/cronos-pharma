package br.symbiosys.solucoes.cronospharma.entidades.iqvia

import br.symbiosys.solucoes.cronospharma.entidades.Integrador
import br.symbiosys.solucoes.cronospharma.entidades.TipoIntegracao
import br.symbiosys.solucoes.cronospharma.entidades.cronos.ItemPedidoPalm
import br.symbiosys.solucoes.cronospharma.entidades.cronos.PedidoPalm
import br.symbiosys.solucoes.cronospharma.entidades.diretorios.Diretorio
import org.apache.commons.lang3.StringUtils
import java.io.File
import java.time.LocalDateTime

class PedidoIqvia (
    val tipoIntegracao: TipoIntegracao = TipoIntegracao.REDEFTB,
    val cnpjCliente: String? = null,
    val tipoFaturamento: Int? = null,
    val apontadorPromocao: String? = null,
    val descontoDistribuidorOuVan: String? = null,
    val codigoProjeto: String? = null,
    val numeroPedidoIqvia: String? = null,
    val cnpjCD: String? = null,
    val tipoPagamento: String? = null,
    val codigoPrazoDeterminado: String? = null,
    val numeroDiasPrazoDeterminado: String? = null,
    val numeroPedidoPrincipal: String? = null,
    val numeroPedidoCliente: String? = null,
    val dataPedido: LocalDateTime? = null,
    val itensPedidoIqvia: List<ItemPedidoIqvia> = mutableListOf(),
    val quatidadeItems: Int? = null,
    ) : Integrador

{
    constructor() : this(
        TipoIntegracao.REDEFTB
    )


    override fun toPedidoPalm(): PedidoPalm {
        return PedidoPalm(
            Origem = "REDEFTB",
            NumPedidoPalm = numeroPedidoIqvia ?: "",
            CnpjCpfCliFor = cnpjCliente,
            DataPedido = dataPedido,
            NumPedidoPalmAux = numeroPedidoCliente,
            itens = itensPedidoIqvia.map { ItemPedidoPalm(
                CodProdutoArq = it.codigoEAN ?: "",
                Qtd = it.quantidade,
                PercDescontoItem = it.descontoItem,
                Item = itensPedidoIqvia.indexOf(it) + 1
            ) }
        )
    }

    override fun gerarRetorno(cnpj: String, pedidoPalm: PedidoPalm, diretorio: Diretorio): File {
        var conteudo: StringBuilder = java.lang.StringBuilder()
        val data = LocalDateTime.now().toString()

        //cabecalho
        conteudo.append("1${pedidoPalm.CnpjCpfCliFor} ${StringUtils.leftPad(pedidoPalm.NumPedidoPalm, 15, " ")}00\n")

        //dados pedido
        conteudo.append("2${pedidoPalm.DataPedido.toString().substring(8,10)}${pedidoPalm.DataPedido.toString().substring(5,7)}" +
                "${pedidoPalm.DataPedido.toString().substring(0,4)}${pedidoPalm.DataPedido.toString().substring(11, 13)}" +
                "${pedidoPalm.DataPedido.toString().substring(14,16)}${pedidoPalm.DataPedido.toString().substring(17,19)}${StringUtils.rightPad("",10, "0")}\n")

        //itens do pedido
        pedidoPalm.itens.forEach {
            val quantidadeNaoAtendida = it.Qtd.minus(it.QtdConfirmada!!).toInt()
            conteudo.append("3")
            conteudo.append(it.CodProdutoArq)
            conteudo.append(StringUtils.leftPad(it.QtdConfirmada?.toInt().toString(), 8, "0"))
            conteudo.append(StringUtils.leftPad(quantidadeNaoAtendida.toString(), 8, "0"))
            conteudo.append(it.CodRetornoItem)

            if(quantidadeNaoAtendida > 0 && it.SituacaoItemPedido == "C") {
                conteudo.append("2")
            }
            if(it.SituacaoItemPedido == "I") {
                conteudo.append("3")
            }
            if(quantidadeNaoAtendida <= 0 && it.SituacaoItemPedido == "C") {
                conteudo.append("1")
            }
            conteudo.append("\n")
        }

        // fim do arquivo
        var quantidadeAtendidas = 0
        var quantidadeNaoAtendida = 0
        pedidoPalm.itens.forEach {
            quantidadeAtendidas += it.QtdConfirmada!!.toInt()
            quantidadeNaoAtendida += (it.Qtd - it.QtdConfirmada).toInt()
        }
        conteudo.append("4${StringUtils.leftPad(quantidadeAtendidas.toString(), 8, "0")}" +
                "${StringUtils.leftPad(quantidadeNaoAtendida.toString(), 8, "0")}" +
                "${StringUtils.leftPad(pedidoPalm.itens.size.toString(), 8, "0")}" +
                "${StringUtils.leftPad("", 10, "0")}")

        //nome do arquivo
        val nomeArquivo = "RETNGEO_${cnpj}_${pedidoPalm.NumPedidoPalm}.RET"
        val file = File(diretorio.diretorioRetornoLocal + nomeArquivo)
        file.writeText(conteudo.toString())
        return file
    }
}