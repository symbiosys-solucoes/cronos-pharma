package br.symbiosys.solucoes.cronospharma.modules.cronos.agendamento

import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class AgendamentoRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {

    fun findAll(): List<Agendamento>{
        val query = "SELECT  IdAgendamento, NomeAgendamento, Ativa, CRON = Sqlquery FROM RAgendamento where IdUsuario = 'sym' and Ativa = 'S'"
        return  jdbcTemplate.query(
            query,mapper
        )
    }

    fun findByType(type: TipoAgendamentoEnum): List<Agendamento>{
        val query = "SELECT  IdAgendamento, NomeAgendamento, Ativa, CRON = Sqlquery FROM RAgendamento where IdUsuario = 'sym' and Ativa = 'S' AND NomeAgendamento = :type"
        return  jdbcTemplate.query(
            query, mapOf("type" to type.name),mapper
        )
    }



    private val mapper =  RowMapper<Agendamento> { rs: ResultSet, rowNum: Int ->
        Agendamento().apply {
            id = rs.getInt("IdAgendamento")
            nomeAgendamento = TipoAgendamentoEnum.valueOf(rs.getString("NomeAgendamento"))
            ativa =  if( rs.getString("Ativa") == "S") true else false
            cron = rs.getString("CRON")

        }
    }
}