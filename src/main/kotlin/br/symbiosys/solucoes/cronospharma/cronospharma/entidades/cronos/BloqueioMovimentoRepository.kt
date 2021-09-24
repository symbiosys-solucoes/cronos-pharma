package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos

import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class BloqueioMovimentoRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {
    val logger = LoggerFactory.getLogger(BloqueioMovimentoRepository::class.java)
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
                        { rs: ResultSet, rowNum: Int -> rs.getString(1) }
                    )
                }

            }

        }


    }

    fun executaRegrasMovimento(idMov: Long): List<RetornoRegras> {

        val listaDeResultados = mutableListOf<RetornoRegras>()

        val regrasAbortaFinalizacao = jdbcTemplate.query(sqlTipoBloqueioByTipoAndAcao, MapSqlParameterSource("tipoAcao", "FIN").addValue("tipoBloqueio", "COM"), mapperBloqueioMovimento)
        val regrasBloqueioComercial = jdbcTemplate.query(sqlTipoBloqueioByTipoAndAcao, MapSqlParameterSource("tipoAcao", "AUT").addValue("tipoBloqueio", "COM"), mapperBloqueioMovimento)
        val regrasBloqueioFinanceiro = jdbcTemplate.query(sqlTipoBloqueioByTipoAndAcao, MapSqlParameterSource("tipoAcao", "AUF").addValue("tipoBloqueio", "COM"), mapperBloqueioMovimento)

        regrasAbortaFinalizacao.forEach {
            try {
                if(it.ExpressaoSQL != null){
                    val resultado = jdbcTemplate.queryForObject(
                        it.ExpressaoSQL!!.uppercase(),
                        MapSqlParameterSource("PCHAR1","2.1").addValue("IDMOV", idMov),
                        String::class.java
                    )
                    listaDeResultados.add(RetornoRegras(it.NomeBloqueio, resultado, "FINALIZACAO"))
                }
            } catch (e: Exception){
                logger.error("Erro ao executar regra: ${it.NomeBloqueio} | ${e.message}")
            }

        }

        regrasBloqueioComercial.forEach {
            try {
                if(it.ExpressaoSQL != null){
                    val resultado = jdbcTemplate.queryForObject(
                        it.ExpressaoSQL!!.uppercase(),
                        MapSqlParameterSource("PCHAR1","2.1").addValue("IDMOV", idMov),
                        String::class.java
                    )
                    listaDeResultados.add(RetornoRegras(it.NomeBloqueio, resultado, "COMERCIAL"))
                }
            } catch (e: Exception){
                logger.error("Erro ao executar regra: ${it.NomeBloqueio} | ${e.message}")
            }

        }

        regrasBloqueioFinanceiro.forEach {
            try {
                if(it.ExpressaoSQL != null){
                    val resultado = jdbcTemplate.queryForObject(
                        it.ExpressaoSQL!!.uppercase(),
                        MapSqlParameterSource("PCHAR1","2.1").addValue("IDMOV", idMov),
                        String::class.java
                    )
                    listaDeResultados.add(RetornoRegras(it.NomeBloqueio, resultado, "FINANCEIRO"))
                }
            } catch (e: Exception){
                logger.error("Erro ao executar regra: ${it.NomeBloqueio} | ${e.message}")
            }

        }

        return listaDeResultados

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
                DataOperacao = rs.getTimestamp("DataOperacao")?.toLocalDateTime() ?: null,
                IdUsuario = rs.getString("IdUsuario"),
                DataUltAlteracao = rs.getTimestamp("DataUltAlteracao")?.toLocalDateTime() ?: null,
                UserUltAlteracao = rs.getString("UserUltAlteracao")
            )
        }
        private val sqlTipoBloqueioByTipo = "" +
                "\n" +
                "SELECT * FROM BloqueioMovimento WHERE TipoBloqueio = :tipoBloqueio"

        private val sqlTipoBloqueioByTipoAndAcao = "" +
                "\n" +
                "SELECT * FROM BloqueioMovimento WHERE TipoBloqueio = :tipoBloqueio AND TipoAcao = :tipoAcao AND ISNULL(Inativo, 'N') = 'N'"
    }

}

data class RetornoRegras(
    val nome: String?,
    val ok: String?,
    val tipo: String?
)