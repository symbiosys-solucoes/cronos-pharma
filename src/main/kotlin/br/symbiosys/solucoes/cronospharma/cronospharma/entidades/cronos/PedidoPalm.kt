package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos

import java.math.BigDecimal
import java.time.LocalDateTime

class PedidoPalm (
    val IdPedidoPalm: Long? = null,
    val  Origem: String,
    val  CodFilial:String = "01",
    val  IdEmpresa: Long = 1,
    val  NumPedidoPalm:String,
    val  CodVendedor:String?,
    val  CodCliFor:String?,
    val  CnpjCpfCliFor:String?,
    val  DataPedido: LocalDateTime?,
    val  PercComissao:Double?,
    val  CodCondPag:String?,
    val  CodPortador:String?,
    val  PercDesconto:Double?,
    val  TotalPedido: BigDecimal?,
    val  DataEntrega:LocalDateTime?,
    val  Observacoes:String?,
    val  IdExpedicao:Long?,
    val  DataProxVisita:LocalDateTime?,
    val  SituacaoPedido:String = "P",
    val  NumNF:String?,
    val  LogImportacao:String?,
    val  StatusRetorno:String?,
    val  CodRetorno:String?,
    val  DscRetorno:String,
    val  NumPedidoCRONOS:String?,
    val  ArqRetPed:String?,
    val  ArqRetNF:String?,
    val  ArqRet2Ped:String?,
    val  VerLayout:String?,
    val  NumPedidoPalmAux:String?,
    val  IdUsuario:String = "SYM",
    val  DataOperacao:LocalDateTime = LocalDateTime.now(),
    val itens:List<ItemPedidoPalm>,

        ){

}