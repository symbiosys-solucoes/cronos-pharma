package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.repositories

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class PedidoPalmPetronasRepository {

    @Autowired
    private lateinit var jdbcTemplate: NamedParameterJdbcTemplate


    fun save(pedido: PedidoPalmPetronas): PedidoPalmPetronas {

        val queryInsertPedido = "" +
                "DECLARE @CODCOND VARCHAR(2), @CODPORTADOR VARCHAR(2)\n" +
                "SET @CODCOND = (SELECT ISNULL(CodCondPag,'01') FROM CondPag WHERE CondPag = :codcondicao)\n" +
                "SET @CODPORTADOR = (SELECT ISNULL(CodPortador,'01') FROM Portador WHERE NomePortador = :codportador)\n" +
                "\n" +
                "INSERT INTO PedidoPalm\n" +
                "(Origem, IdEmpresa, NumPedidoPalm, CodVendedor, CodCliFor,  DataPedido, PercComissao, CodCondPag, CodPortador, PercDesconto, TotalPedido, DataEntrega, Observacoes,  SituacaoPedido,  LogImportacao, IdUsuario, DataOperacao, CodFilial, NumPedidoPalmAux) OUTPUT INSERTED.*\n" +
                "VALUES(:origem, 1, :numpedido, :codvendedor, :codcli, :dtpedido, :perccomissao, @CODCOND, @CODPORTADOR, :percdesconto, :totalpedido, :dataentrega, :observacoes, :situacao, :logimport, :idusuario, :dataoperacao, :codfilial, :numeroaux);"

        val params = MapSqlParameterSource("origem", pedido.origem)
            .addValue("numpedido", pedido.numeroPedido)
            .addValue("codvendedor", pedido.codigoVendedor)
            .addValue("codcli", pedido.codigoCliente).addValue("dtpedido", pedido.dataPedido)
            .addValue("perccomissao", pedido.percentualComissao).addValue("codcondicao", pedido.condicaoPagamento)
            .addValue("codportador", pedido.codigoPortador).addValue("percdesconto", pedido.percentualDesconto)
            .addValue("totalpedido", pedido.totalPedido)
            .addValue("dataentrega", pedido.dataEntrega).addValue("observacoes", pedido.observacoes)
            .addValue("situacao", pedido.situacaoPedido).addValue("logimport", pedido.logImportacao)
            .addValue("idusuario", pedido.idUsuario).addValue("dataoperacao", pedido.dataOperacao)
            .addValue("codfilial", pedido.codigoFilial).addValue("numeroaux", pedido.numeroPedidoPalmAux)

        return jdbcTemplate.query(queryInsertPedido, params, mapperPedidoPalmPetronas).first()
    }

    companion object {
        private val mapperPedidoPalmPetronas = RowMapper<PedidoPalmPetronas> { rs: ResultSet, rowNum: Int ->
            PedidoPalmPetronas(
            ).apply {
                numeroPedido = rs.getString("NumPedidoPalm")
                codigoVendedor = rs.getString("CodVendedor")
                codigoCliente = rs.getString("CodCliFor")
                dataPedido = rs.getTimestamp("DataPedido").toLocalDateTime()
                dataEntrega = rs.getTimestamp("DataEntrega")?.toLocalDateTime()
                percentualComissao = rs.getDouble("PercComissao")
                condicaoPagamento = rs.getString("CodCondPag")
                codigoPortador = rs.getString("CodPortador")
                percentualDesconto = rs.getDouble("PercDesconto")
                totalPedido = rs.getDouble("TotalPedido")
                observacoes = rs.getString("Observacoes")
                situacaoPedido = rs.getString("SituacaoPedido")
                logImportacao = rs.getString("LogImportacao")
                dataOperacao = rs.getTimestamp("DataOperacao").toLocalDateTime()
                codigoFilial = rs.getString("CodFilial")
                numeroPedidoPalmAux = rs.getString("NumPedidoPalmAux")
            }
        }
    }
}