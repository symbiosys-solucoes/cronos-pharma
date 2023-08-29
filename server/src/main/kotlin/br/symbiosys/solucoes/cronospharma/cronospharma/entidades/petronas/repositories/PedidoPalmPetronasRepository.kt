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

    fun save(item: ItemPedidoPalmPetronas, numeroPedidoPetronas: String, codigoFilial: String): ItemPedidoPalmPetronas {

        val query = "" +
                "DECLARE @IDPEDIDOPALM INT\n" +
                "DECLARE @IDPRODUTO INT \n" +
                "DECLARE @ItemPedido Table (id int) \n" +
                "SET @IDPEDIDOPALM = ISNULL((SELECT IdPedidoPalm FROM PedidoPalm WHERE NumPedidoPalm = :numpedido AND CodFilial = :codfilial),0) AND SituacaoPedido = 'P'\n" +
                "IF :dscretorno = 'PETRONAS'\n" +
                "BEGIN\n" +
                "SET @IDPRODUTO = ISNULL((SELECT IdProduto FROM PRODUTOS WHERE CodProdutoFabr = :codproduto),0)\n" +
                "END\n" +
                "ELSE\n" +
                "BEGIN\n" +
                "SET @IDPRODUTO = ISNULL((SELECT IdProduto FROM PRODUTOS WHERE CodProduto = :codproduto),0)\n" +
                "END\n" +
                "\n" +
                "IF @IDPEDIDOPALM = 0\n" +
                "THROW 51000, 'The record does not exist.', 1;" +
                "\n" +
                "INSERT INTO ItemPedidoPalm\n" +
                "( IdPedidoPalm, Item, IdEmpresa, CodProdutoArq, IdProduto, CodProduto, Qtd, QtdConfirmada, IdPrecoTabela, PrecoUnit, PercDescontoItem, SituacaoItemPedido, LogImportacao, DscRetornoItem, IdUsuario, DataOperacao) OUTPUT INSERTED.IdItemPedidoPalm INTO @ItemPedido \n" +
                "VALUES(@IDPEDIDOPALM, :sequencia, 1, :codproduto, @IDPRODUTO, ISNULL((SELECT CODPRODUTO FROM Produtos WHERE IdProduto = @IDPRODUTO),0), :qtd, 0, :tabela, :preco, :percdesc, :situacao, :logimport, :dscretorno, :idusuario, :data);" +
                "\n" +
                "SELECT * FROM ItemPedidoPalm where IdItemPedidoPalm = (SELECT id FROM @ItemPedido)"

        val params = MapSqlParameterSource("dscretorno", item.descricaoRetornoItem).addValue("numpedido", numeroPedidoPetronas).addValue("codfilial", codigoFilial)
            .addValue("codproduto", item.codigoProdutoArquivo).addValue("qtd", item.quantidadeSolicitada).addValue("sequencia", item.sequencialItem)
            .addValue("tabela", item.idPrecoTabela).addValue("preco", item.precoUnitario).addValue("percdesc", item.percentualDesconto).addValue("situacao", item.situacaoItem)
            .addValue("logimport", item.logImportacao).addValue("idusuario", item.idUsuario).addValue("data", item.dataOperacao)

        return jdbcTemplate.query(query, params, mapperItemPedidoPalmPetronas).first()
    }


    companion object {

        private val mapperItemPedidoPalmPetronas = RowMapper<ItemPedidoPalmPetronas> { rs: ResultSet, rowNum: Int ->
            ItemPedidoPalmPetronas().apply {
                idPedidoPalmPetronas = rs.getInt("IdPedidoPalm")
                idItemPedido = rs.getInt("IdItemPedidoPalm")
                sequencialItem = rs.getInt("Item")
                codigoProdutoArquivo = rs.getString("CodProdutoArq")
                idProduto = rs.getInt("IdProduto")
                codigoProduto = rs.getString("CodProduto")
                quantidadeSolicitada = rs.getDouble("Qtd")
                quantidadeConfirmada = rs.getDouble("QtdConfirmada")
                idPrecoTabela = rs.getInt("IdPrecoTabela")
                precoUnitario = rs.getDouble("PrecoUnit")
                percentualDesconto = rs.getDouble("PercDescontoItem")
                situacaoItem = rs.getString("SituacaoItemPedido")
                logImportacao = rs.getString("LogImportacao")
                codigoRetornoItem = rs.getString("CodRetornoItem")
                descricaoRetornoItem = rs.getString("DscRetornoItem")
                dataOperacao = rs.getTimestamp("DataOperacao").toLocalDateTime()
            }
        }

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