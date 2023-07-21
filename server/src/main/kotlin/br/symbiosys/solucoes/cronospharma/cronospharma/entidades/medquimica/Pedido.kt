package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.medquimica

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.Integrador
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos.ItemPedidoPalm
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos.PedidoPalm
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.diretorios.Diretorio
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

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
        TODO("Not yet implemented")
    }


}