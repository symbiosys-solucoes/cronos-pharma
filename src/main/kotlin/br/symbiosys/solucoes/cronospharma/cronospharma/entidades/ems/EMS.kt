package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.ems

import java.time.LocalDate

class EMS (
    val codigoCliente: String?,
    val numeroPedido: String?,
    val dataPedido: LocalDate?,
    val tipoCompra: String? = null,
    val tipoRetorno: String? = null,
    val apontadorCondicaoComercial: String? = null,
    val numeroPedidoCliente: String? = null,
    val prazo: String? = null,
    val codigoRepresentante: String? = null,
    val produtos: List<ItemEMS>?
        ){
}