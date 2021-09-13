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
                Item = produtos.indexOf(it)
            ) }

        )
    }

    override fun gerarRetorno(cnpj: String, pedidoPalm: PedidoPalm, diretorio: Diretorio): File {
        var conteudo: StringBuilder = StringBuilder()
        val data = LocalDateTime.now()
        conteudo.append("0RETORNO PED OL 0${cnpj}${data.dayOfMonth}${data.monthValue}${data.year}${data.hour}${data.minute}${data.second}${data.nano}\n")

        conteudo.append("10${this.codigoCliente}${StringUtils.rightPad(numeroPedido,12," ")}"+
                "${data.dayOfMonth}${data.monthValue}${data.year}${data.hour}${data.minute}${data.second}${data.nano}"+
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

        val nomeDoArquivo = "RETEMS_${cnpj}_${data.year}${data.monthValue}${data.dayOfMonth}${data.hour}${data.minute}${data.second}${data.nano}.txt"

        val file = File(diretorio.diretorioRetornoLocal + nomeDoArquivo)
        file.writeText(conteudo.toString())
        return file
    }

}

