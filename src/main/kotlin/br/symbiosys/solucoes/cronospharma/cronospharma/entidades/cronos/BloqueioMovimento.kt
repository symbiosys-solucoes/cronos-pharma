package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos

import java.time.LocalDateTime


class BloqueioMovimento(
    var IdBloqueioMovimento: Long? = null,
    var TipoBloqueio: String? = null,
    var NomeBloqueio: String? = null,
    var ExpressaoSQL: String? = null,
    var Inativo: String? = "N",
    var TipoAcao: String? = null,
    var OrdemExec: Int? = null,
    var DataOperacao: LocalDateTime? = null,
    var IdUsuario: String? = null,
    var DataUltAlteracao: LocalDateTime? = null,
    var UserUltAlteracao: String? = null

) {
}