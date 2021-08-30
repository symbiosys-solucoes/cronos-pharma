package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.consys

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.TipoIntegracao
import java.time.LocalDateTime


class PedidoCONSYS (
    val tipoIntegracao: TipoIntegracao = TipoIntegracao.CONSYS,
    val cliente: String,
    val idPedido: String,
    val cnpj: String,
    val dataHora: LocalDateTime,

    val produtos: List<ItemCONSYS>
        ){

}