package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.closeup

import java.time.LocalDateTime

class Estoque {

    val tipoRegistro = "1"
    var cnpjDistribuidor : String? = null
    var dataPosicaoEstoque : String? = null

    var items : MutableList<ItemEstoque> = mutableListOf()
}