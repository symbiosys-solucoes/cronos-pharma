package br.symbiosys.solucoes.cronospharma.cronospharma.sym.model

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.request.Order
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.request.OrderRequest

interface ISymEventos {

    fun processarEventos(eventos: MutableList<SymEventos>): MutableList<OrderRequest>
    fun filtrarRegistros(eventos: List<SymEventos>): MutableList<SymEventos> {
        val sorted = eventos.groupBy { it.idRegistro }
        return sorted.values.map { it.sortedByDescending { it.dataEvento }.first() }.toMutableList()
    }
}