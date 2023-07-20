package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.medquimica

import java.time.LocalDateTime

class Estoque {

    val tipoRegistro = "1"
    var cnpjDistribuidor : String? = null
    var dataPosicaoEstoque : LocalDateTime? = null

    var items : MutableList<ItemEstoque> = mutableListOf()
}