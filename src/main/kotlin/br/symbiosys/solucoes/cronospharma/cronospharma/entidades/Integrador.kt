package br.symbiosys.solucoes.cronospharma.cronospharma.entidades

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos.PedidoPalm

interface Integrador {
    fun toPedidoPalm(): PedidoPalm
}