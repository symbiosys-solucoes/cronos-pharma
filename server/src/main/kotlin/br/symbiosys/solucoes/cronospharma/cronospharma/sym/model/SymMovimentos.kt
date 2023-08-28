package br.symbiosys.solucoes.cronospharma.cronospharma.sym.model

import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class SymMovimentos {

    @Id
    @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
    var id: Long? = null

    var idPedidoPalm: Long? = null

    var idPetronas: Long? = null

    var idOrcamento: Long? = null

    var dataCriacaoOrcamento: LocalDateTime? = null

    var idPreVenda: Long? = null

    var dataCriacaoPreVenda : LocalDateTime? = null

    var idNotaFiscal: Long? = null

    var dataCriacaoNotaFiscal: LocalDateTime? = null

    var dataOperacao: LocalDateTime? = null

}