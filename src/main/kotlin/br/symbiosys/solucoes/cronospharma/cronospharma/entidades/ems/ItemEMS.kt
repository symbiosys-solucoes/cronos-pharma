package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.ems


class ItemEMS (
    val codigoProduto: String,
    val quantidade: Double,
    val desconto: Double,
    val prazo: String? = null,
    val ulizaDesconto: String? = null,
    val utilizaPrazo: String? = null
        ) {

}
