package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos

import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class BloqueioMovimentoRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {
    fun findByTipoBloqueio(tipoBloqueio: String): List<BloqueioMovimento>{

       return jdbcTemplate.query(sqlTipoBloqueioByTipo, MapSqlParameterSource().addValue("tipoBloqueio", tipoBloqueio),
           mapperBloqueioMovimento )


    }

    fun executaRegrasTipoGravarRetornos(pedido: PedidoPalm) {

        if(pedido.IdPedidoPalm != null && pedido.NumPedidoCRONOS != null && pedido.SituacaoPedido != "P"){

            val regras = findByTipoBloqueio("023")

            regras.forEach {
                if(it.ExpressaoSQL != null && it.Inativo != "S"){

                    jdbcTemplate.query(it.ExpressaoSQL!!.uppercase(),
                        MapSqlParameterSource().addValue("PINT1",pedido.IdPedidoPalm),
                        RowMapper<String> { rs: ResultSet, rowNum: Int -> rs.getString(1) }
                    )
                }

            }

        }


    }

    companion object{
        private val mapperBloqueioMovimento = RowMapper<BloqueioMovimento> {rs: ResultSet, rowNum: Int ->
            BloqueioMovimento(
                IdBloqueioMovimento = rs.getLong("IdBloqueioMovimento"),
                TipoBloqueio = rs.getString("TipoBloqueio"),
                NomeBloqueio = rs.getString("NomeBloqueio"),
                ExpressaoSQL = rs.getString("ExpressaoSQL"),
                Inativo = rs.getString("Inativo"),
                TipoAcao = rs.getString("TipoAcao"),
                OrdemExec = rs.getInt("OrdemExec"),
                DataOperacao = rs.getTimestamp("DataOperacao").toLocalDateTime(),
                IdUsuario = rs.getString("IdUsuario"),
                DataUltAlteracao = rs.getTimestamp("DataUltAlteracao").toLocalDateTime(),
                UserUltAlteracao = rs.getString("UserUltAlteracao")
            )
        }
        private val sqlTipoBloqueioByTipo = "" +
                "\n" +
                "SELECT * FROM BloqueioMovimento WHERE TipoBloqueio = :tipoBloqueio"
    }

}