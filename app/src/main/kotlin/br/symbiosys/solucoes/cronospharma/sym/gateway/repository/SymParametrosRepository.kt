package br.symbiosys.solucoes.cronospharma.sym.gateway.repository

import br.symbiosys.solucoes.cronospharma.sym.model.SymParametros
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class SymParametrosRepository (private val jdbcTemplate: NamedParameterJdbcTemplate) {

    val mapper = ObjectMapper()


    fun findByCodfilial(codfilial: String): SymParametros {
        val parametrosString = jdbcTemplate.queryForObject("SELECT sym_parametros FROM ZFiliaisCompl WHERE CodFilial = :codfilial", MapSqlParameterSource("codfilial", codfilial), String::class.java)
        return mapper.readValue(parametrosString, SymParametros::class.java)
    }
    fun findAll(): List<SymParametros> {
        val parametrosString = jdbcTemplate.queryForList("SELECT sym_parametros FROM ZFiliaisCompl", MapSqlParameterSource(),String::class.java)
        return parametrosString.map { mapper.readValue(it, SymParametros::class.java) }
    }

    fun save(parametros: SymParametros): SymParametros {
        val parametrosString = mapper.writeValueAsString(parametros)
        jdbcTemplate.update("UPDATE ZFiliaisCompl SET sym_parametros = :sym_parametros WHERE CodFilial = :codfilial",
            MapSqlParameterSource("codfilial", parametros.codigoFilial).addValue("sym_parametros", parametrosString))
        return findByCodfilial(parametros.codigoFilial!!)
    }
}