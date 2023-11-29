package br.symbiosys.solucoes.cronospharma.entidades.cronos

import java.math.BigDecimal
import java.time.LocalDateTime

class PedidoPalm (
    val IdPedidoPalm: Long? = null,
    val  Origem: String,
    val  CodFilial:String = "01",
    val  IdEmpresa: Long = 1,
    val  NumPedidoPalm:String,
    var  CodVendedor:String? = null,
    var  CodCliFor:String? = null,
    val  CnpjCpfCliFor:String? = null,
    val  DataPedido: LocalDateTime? = null,
    val  PercComissao:Double? = null,
    var  CodCondPag:String? = null,
    var  CodPortador:String? = null,
    val  PercDesconto:Double? = null,
    val  TotalPedido: BigDecimal? = null,
    val  DataEntrega:LocalDateTime? = null,
    val  Observacoes:String? = null,
    val  IdExpedicao:Long? = null,
    val  DataProxVisita:LocalDateTime? = null,
    var SituacaoPedido:String = "P",
    val  NumNF:String? = null,
    val  LogImportacao:String? = null,
    val  StatusRetorno:String? = null,
    val  CodRetorno:String? = null,
    val  DscRetorno:String? = null,
    val  NumPedidoCRONOS:String? = null,
    val  ArqRetPed:String? = null,
    val  ArqRetNF:String? = null,
    val  ArqRet2Ped:String? = null,
    val  VerLayout:String? = null,
    val  NumPedidoPalmAux:String? = null,
    val  IdUsuario:String = "SYM",
    val  DataOperacao:LocalDateTime = LocalDateTime.now(),
    var itens:List<ItemPedidoPalm>,

    ){

    override fun toString(): String {
        return "PedidoPalm(IdPedidoPalm=$IdPedidoPalm, Origem='$Origem', CodFilial='$CodFilial', IdEmpresa=$IdEmpresa, NumPedidoPalm='$NumPedidoPalm', CodVendedor=$CodVendedor, CodCliFor=$CodCliFor, CnpjCpfCliFor=$CnpjCpfCliFor, DataPedido=$DataPedido, PercComissao=$PercComissao, CodCondPag=$CodCondPag, CodPortador=$CodPortador, PercDesconto=$PercDesconto, TotalPedido=$TotalPedido, DataEntrega=$DataEntrega, Observacoes=$Observacoes, IdExpedicao=$IdExpedicao, DataProxVisita=$DataProxVisita, SituacaoPedido='$SituacaoPedido', NumNF=$NumNF, LogImportacao=$LogImportacao, StatusRetorno=$StatusRetorno, CodRetorno=$CodRetorno, DscRetorno=$DscRetorno, NumPedidoCRONOS=$NumPedidoCRONOS, ArqRetPed=$ArqRetPed, ArqRetNF=$ArqRetNF, ArqRet2Ped=$ArqRet2Ped, VerLayout=$VerLayout, NumPedidoPalmAux=$NumPedidoPalmAux, IdUsuario='$IdUsuario', DataOperacao=$DataOperacao, itens=$itens)"
    }
}