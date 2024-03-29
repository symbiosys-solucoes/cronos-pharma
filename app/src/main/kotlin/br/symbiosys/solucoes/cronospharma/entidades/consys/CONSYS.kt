package br.symbiosys.solucoes.cronospharma.entidades.consys

import br.symbiosys.solucoes.cronospharma.entidades.Integrador
import br.symbiosys.solucoes.cronospharma.entidades.TipoIntegracao
import br.symbiosys.solucoes.cronospharma.entidades.cronos.ItemPedidoPalm
import br.symbiosys.solucoes.cronospharma.entidades.cronos.PedidoPalm
import br.symbiosys.solucoes.cronospharma.entidades.diretorios.Diretorio
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime


class PedidoCONSYS (
    val tipoIntegracao: TipoIntegracao = TipoIntegracao.CONSYS,
    val cliente: String?,
    val idPedido: String?,
    val cnpj: String?,
    val dataPedido: LocalDate?,

    val produtos: List<ItemCONSYS>
): Integrador{
    override fun toString(): String {
        return "PedidoCONSYS(tipoIntegracao=$tipoIntegracao, cliente=$cliente, idPedido=$idPedido, cnpj=$cnpj, dataPedido=$dataPedido, produtos=$produtos)"
    }

    override fun toPedidoPalm(): PedidoPalm {
        return PedidoPalm(
            Origem = tipoIntegracao.name,
            CnpjCpfCliFor = cnpj,
            NumPedidoPalm = idPedido ?: "",
            DataPedido = LocalDateTime.of(dataPedido, LocalTime.now()),
            NumPedidoPalmAux = idPedido,
            itens = produtos.map { ItemPedidoPalm(
                CodProdutoArq = it.codigo,
                Qtd = it.quantidade,
                LogImportacao = it.codigoCONSYS,
                Item = produtos.indexOf(it) + 1
            ) }

        )
    }

    override fun gerarRetorno(cnpj: String, pedidoPalm: PedidoPalm, diretorio: Diretorio): File {
        return File(".")
    }


}