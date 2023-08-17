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

    @Enumerated(EnumType.STRING)
    lateinit var tipo: TipoEvento

    lateinit var tabela: String

    lateinit var idRegistro: String

    lateinit var usuario: String

    lateinit var oldRegisterAsJson: String

    lateinit var newRegisterAsJson: String

    lateinit var dataEvento: LocalDateTime
}