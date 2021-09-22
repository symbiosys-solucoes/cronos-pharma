package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.ems

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.Integrador
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.TipoIntegracao
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos.ItemPedidoPalm
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos.PedidoPalm
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.diretorios.Diretorio
import br.symbiosys.solucoes.cronospharma.cronospharma.processamento.Arquivo
import org.apache.commons.lang3.StringUtils

import java.io.File
import java.lang.StringBuilder
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class EMS(
    val tipoIntegracao: TipoIntegracao = TipoIntegracao.EMS,
    val codigoCliente: String?,
    val numeroPedido: String?,
    val dataPedido: LocalDate?,
    val tipoCompra: String? = null,
    val tipoRetorno: String? = null,
    val apontadorCondicaoComercial: String? = null,
    val numeroPedidoCliente: String? = null,
    val prazo: String? = null,
    val codigoRepresentante: String? = null,
    val produtos: List<ItemEMS>
        ): Integrador{

        constructor(p: PedidoPalm) : this(
            codigoCliente = p.CnpjCpfCliFor,
            numeroPedido = p.NumPedidoPalm,
            dataPedido = p.DataPedido?.toLocalDate() ?: LocalDate.now(),
            produtos = listOf()
        )

    override fun toPedidoPalm(): PedidoPalm {

        return PedidoPalm(
            Origem = tipoIntegracao.name,
            CnpjCpfCliFor = codigoCliente,
            NumPedidoPalm = numeroPedido ?: "",
            DataPedido = LocalDateTime.of(dataPedido, LocalTime.now()),
            NumPedidoPalmAux = numeroPedidoCliente,
            itens = produtos.map { ItemPedidoPalm(
                CodProdutoArq = it.codigoProduto,
                Qtd = it.quantidade,
                PercDescontoItem = it.desconto,
                Item = produtos.indexOf(it) + 1
            ) }

        )
    }

    override fun gerarRetorno(cnpj: String, pedidoPalm: PedidoPalm, diretorio: Diretorio): File {
        var conteudo: StringBuilder = StringBuilder()
        val data = LocalDateTime.now().toString()
        conteudo.append("0RETORNO PED OL 0${cnpj}${data.substring(8,10)}${data.substring(5,7)}${data.substring(0,4)}${data.substring(11,13)}${data.substring(14,16)}${data.substring(17,19)}00\n")

        conteudo.append("10${pedidoPalm.CnpjCpfCliFor}${StringUtils.rightPad(numeroPedido,12," ")}"+
                "${cnpj}${data.substring(8,10)}${data.substring(5,7)}${data.substring(0,4)}${data.substring(11,13)}${data.substring(14,16)}${data.substring(17,19)}00"+
                "${pedidoPalm.NumPedidoCRONOS}${pedidoPalm.CodRetorno}\n")
        val R2 = pedidoPalm.itens.map {
            "2${it.CodProdutoArq}${StringUtils.rightPad(pedidoPalm.NumPedidoPalm, 12, " ")}"+
            "0${StringUtils.leftPad(it.QtdConfirmada.toString().replace(".",""), 5, "0")}" +
            "${it.PercDescontoItem.toString().replace(".","")}00030" +
            "${StringUtils.leftPad((it.Qtd - (it.QtdConfirmada ?: 0.0)).toString().replace(".",""),5,"0")}"+
            "${it.CodRetornoItem}${it.DscRetornoItem?.trim()}\n"
        }
        R2.forEach { conteudo.append(it) }

        conteudo.append("3${StringUtils.rightPad(pedidoPalm.NumPedidoPalm,12, " ")}${StringUtils.leftPad(R2.size.toString(),5,"0")}"+
            "${StringUtils.leftPad(R2.size.toString(),5,"0")}${StringUtils.leftPad(R2.size.toString(),5,"0")}")

        val nomeDoArquivo = "RETEMS_${cnpj}_${data.substring(0,4)}${data.substring(5,7)}${data.substring(8,10)}${data.substring(11,13)}${data.substring(14,16)}${data.substring(17,19)}.txt"

        val file = File(diretorio.diretorioRetornoLocal + nomeDoArquivo)
        file.writeText(conteudo.toString())
        return file
    }

}

