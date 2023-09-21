package br.symbiosys.solucoes.cronospharma.modules.petronas.ports.repositories.implementations

import br.symbiosys.solucoes.cronospharma.entidades.cronos.BloqueioMovimentoRepository
import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.Order
import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.OrderItem
import br.symbiosys.solucoes.cronospharma.modules.petronas.ports.repositories.PedidoPalmPetronasRepository
import br.symbiosys.solucoes.cronospharma.modules.petronas.ports.repositories.dao.ItemPedidoPalmPetronas
import br.symbiosys.solucoes.cronospharma.modules.petronas.ports.repositories.dao.PedidoPalmPetronas
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class PedidoPalmPetronasRepositoryImpl(
    private val jdbcTemplate: NamedParameterJdbcTemplate,
    private val bloqueioMovimentoRepository: BloqueioMovimentoRepository
) : PedidoPalmPetronasRepository {

    val logger = LoggerFactory.getLogger(PedidoPalmPetronasRepositoryImpl::class.java)


    override fun findItems(numPedidoCronos: String): List<OrderItem> {
        val mapper = RowMapper<OrderItem> { rs: ResultSet, rowNum: Int ->
            OrderItem().apply {
                orderNumberSfa = rs.getString("SFAOrderNumber")
                orderNumberErp = rs.getString("ERPOrderNumber")
                orderItemNumberSfa = rs.getString("SFAOrderItemNumber")
                orderItemNumberErp = rs.getString("ERPOrderItemNumber")
                productCodeErp = rs.getString("ProductCode")
                orderItemStatus = rs.getString("OrderItemStatus")
                orderQuantity = rs.getDouble("OrderQuantity")
                confirmedQuantity = rs.getDouble("OrderQuantity")
                confirmedDate = rs.getString("ConfirmedDate")
                totalVolume = rs.getDouble("TotalVolume")
                unitPrice = rs.getDouble("UnitPrice")
                lineNetAmount = rs.getDouble("LineNetAmount")
                lineAmount = rs.getDouble("LineAmount")
                discountPercentage = rs.getDouble("DiscountPercentage")
                orderDate = rs.getString("OrderDate")
                itemSequence = rs.getInt("ItemSequence")
                manufacturer = rs.getString("Manufacturer")
                totalCost = rs.getDouble("COGS")
                active = true
                dtCode = rs.getString("DTCode")

            }
        }

        val query = "" +
                "DECLARE @PEDIDO VARCHAR(50)\n" +
                "\n" +
                "SET @PEDIDO = :numpedido\n" +
                "IF EXISTS (SELECT 1 FROM dbo.sym_petronas_order_item WHERE ERPOrderNumber = @PEDIDO and SFAOrderNumber is not null)\n" +
                "\tBEGIN\n" +
                "\t\tSELECT * FROM dbo.sym_petronas_order_item WHERE ERPOrderNumber = @PEDIDO\n" +
                "\tEND\n" +
                "ELSE\n" +
                "\tBEGIN\n" +
                "\tSELECT * FROM dbo.sym_petronas_order_item WHERE ERPOrderNumber = @PEDIDO AND Manufacturer = 'PETRONAS'\n" +
                "\tEND"

        return jdbcTemplate.query(
            query,
            MapSqlParameterSource("numpedido", numPedidoCronos),
            mapper
        )

    }

    override fun markAsSent(idItem: Int, SFAOrderItemNumber: String) {
        jdbcTemplate.update(
            "UPDATE ItensMov SET CampoAlfaImOp1 = :itemnumber WHERE IdItemMov = :iditem",
            MapSqlParameterSource("itemnumber", SFAOrderItemNumber).addValue("iditem", idItem)
        )
    }

    override fun markAsSent(numPedidoCronos: String, atualizado: Boolean) {
        jdbcTemplate.update(
            "UPDATE ZMovimentoCompl SET sym_enviar_petronas = 0 WHERE IdMov = (SELECT IdMov FROM Movimento WHERE NUMMOV = :numpedido)",
            MapSqlParameterSource("numpedido", numPedidoCronos)
        )
    }

    override fun findAll(enviados: Boolean): List<Order> {
        val mapper = RowMapper<Order> { rs: ResultSet, rowNum: Int ->
            Order().apply {
                orderNumberSfa = rs.getString("SFAOrderNumber")
                orderNumberErp = rs.getString("ERPOrderNumber")
                orderSource = rs.getString("OrderSource")
                type = rs.getString("OrderType")
                dtCode = rs.getString("DTCode")
                accountNumber = rs.getString("AccountNumber")
                status = rs.getString("Status")
                orderDate = rs.getString("OrderDate")
                totalQuantity = rs.getDouble("TotalQuantity")
                confirmedQuantity = rs.getDouble("TotalQuantity")
                orderTotalVolume = rs.getDouble("OrderTotalVolume")
                netAmount = rs.getDouble("NetAmount")
                totalAmount = rs.getDouble("TotalAmount")
                paymentMethod = rs.getString("PaymentMethod")
                paymentKeyTerms = rs.getString("PaymenteKeyTerms")
                destination = rs.getString("Destination")
                deliveryType = rs.getString("DeliveryType")
                customerOrderNumber = rs.getString("PONumber")
                userCode = rs.getString("UserCode")
                active = true
            }

        }
        return jdbcTemplate.query("SELECT * FROM sym_petronas_order WHERE PrecisaEnviar = 1", mapper)
    }

    override fun save(pedido: PedidoPalmPetronas): PedidoPalmPetronas {

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

    override fun save(item: ItemPedidoPalmPetronas, numeroPedidoPetronas: String, codigoFilial: String): ItemPedidoPalmPetronas {

        val query = "" +
                "DECLARE @IDPEDIDOPALM INT\n" +
                "DECLARE @IDPRODUTO INT \n" +
                "DECLARE @ItemPedido Table (id int) \n" +
                "SET @IDPEDIDOPALM = ISNULL((SELECT IdPedidoPalm FROM PedidoPalm WHERE NumPedidoPalm = :numpedido AND CodFilial = :codfilial AND SituacaoPedido = 'P'),0) \n" +
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

        val params =
            MapSqlParameterSource("dscretorno", item.descricaoRetornoItem).addValue("numpedido", numeroPedidoPetronas)
                .addValue("codfilial", codigoFilial)
                .addValue("codproduto", item.codigoProdutoArquivo).addValue("qtd", item.quantidadeSolicitada)
                .addValue("sequencia", item.sequencialItem)
                .addValue("tabela", item.idPrecoTabela).addValue("preco", item.precoUnitario)
                .addValue("percdesc", item.percentualDesconto).addValue("situacao", item.situacaoItem)
                .addValue("logimport", item.logImportacao).addValue("idusuario", item.idUsuario)
                .addValue("data", item.dataOperacao)

        return jdbcTemplate.query(query, params, mapperItemPedidoPalmPetronas).first()
    }

    override fun toMovimento(pedido: PedidoPalmPetronas) {
        val query = "exec dbo.sym_converte_pedido :idpedido, :tipomov, :codlocal"
        logger.info(pedido.toString())
        val params =
            MapSqlParameterSource("idpedido", pedido.idPedidoPalm).addValue("tipomov", "2.7").addValue("codlocal", "01")
        val result = jdbcTemplate.queryForObject(query, params, String::class.java)
        if (!result.isNullOrBlank()) {
            val idmov: Int = jdbcTemplate.queryForObject(
                "SELECT ISNULL(MAX(IdMov),0) FROM Movimento WHERE NumMov = :numero",
                MapSqlParameterSource("numero", result),
                Int::class.java
            ) ?: 0
            if (idmov != 0) {
                bloqueioMovimentoRepository.executaRegrasMovimento(idmov.toLong())
            }
        }
    }

    override fun convertAll() {
        val pedidos = jdbcTemplate.query(
            "SELECT * FROM PedidoPalm WHERE SituacaoPedido = 'P' AND Origem = 'DiscoverySFA'",
            mapperPedidoPalmPetronas
        )
        logger.info("Foram encontrados ${pedidos.size} para serem importados")
        for (pedido in pedidos) {
            logger.info("Convertendo pedido ${pedido.numeroPedido} da petronas")
            toMovimento(pedido)
        }
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
                idPedidoPalm = rs.getInt("IdPedidoPalm")
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