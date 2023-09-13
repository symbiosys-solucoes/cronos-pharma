package br.symbiosys.solucoes.cronospharma.sym.model

import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "Zsym_erros")
class SymErros {

    @Id @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
    var id: Long? = null

    var dataOperacao = LocalDateTime.now()

    var tipoOperacao = ""

    @Column(columnDefinition = "TEXT")
    var petronasResponse = ""
}