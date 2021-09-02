package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.ems

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.TipoIntegracao
import java.time.LocalDate

class EMS (
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
    val produtos: List<ItemEMS>?
        ){
}