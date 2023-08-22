package br.symbiosys.solucoes.cronospharma.cronospharma.sym.model

import com.fasterxml.jackson.databind.JsonNode
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "Zsym_eventos")
class SymEventos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    lateinit var tipo: String

    lateinit var tabela: String

    lateinit var idRegistro: String

    lateinit var usuario: String

    @Column(columnDefinition = "TEXT")
    lateinit var oldRegisterAsJson: String

    @Column(columnDefinition = "TEXT")
    lateinit var newRegisterAsJson: String

    lateinit var dataEvento: LocalDateTime

    var processado: Boolean? = false
}