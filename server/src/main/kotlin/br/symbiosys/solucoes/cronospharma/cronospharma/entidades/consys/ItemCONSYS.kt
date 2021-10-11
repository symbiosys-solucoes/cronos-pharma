package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.consys

class ItemCONSYS  (
    val codigo:String,
    val quantidade: Double,
    val codigoCONSYS: String,
){
    override fun toString(): String {
        return "ItemCONSYS(codigo='$codigo', quantidade=$quantidade, codigoCONSYS='$codigoCONSYS')"
    }
}
