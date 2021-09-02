package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.consys

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.TipoIntegracao
import java.time.LocalDate


class PedidoCONSYS (
    val tipoIntegracao: TipoIntegracao = TipoIntegracao.CONSYS,
    val cliente: String?,
    val idPedido: String?,
    val cnpj: String?,
    val dataPedido: LocalDate?,

    val produtos: List<ItemCONSYS>
){
    override fun toString(): String {
        return "PedidoCONSYS(tipoIntegracao=$tipoIntegracao, cliente=$cliente, idPedido=$idPedido, cnpj=$cnpj, dataPedido=$dataPedido, produtos=$produtos)"
    }
}