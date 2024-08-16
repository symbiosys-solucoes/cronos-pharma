package br.symbiosys.solucoes.cronospharma.modules.petronas.ports.repositories.implementations

import br.symbiosys.solucoes.cronospharma.entidades.cronos.FinalizaMovimento
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
import java.math.BigDecimal
import java.sql.ResultSet
import java.time.LocalDate

@Repository
class PedidoPalmPetronasRepositoryImpl(
    private val jdbcTemplate: NamedParameterJdbcTemplate,
    private val finalizaMovimento: FinalizaMovimento
) : PedidoPalmPetronasRepository {

    val logger = LoggerFactory.getLogger(PedidoPalmPetronasRepositoryImpl::class.java)

    override fun deletePedidoPalmPetronas(numPedido: String) {
        val query = " DECLARE @IDPEDIDO INT\n" +
                " SET @IDPEDIDO = (SELECT IdPedidoPalm FROM PedidoPalm WHERE NumPedidoPalm = :numPedido)\n" +
                "\n" +
                " IF EXISTS (SELECT 1 FROM Movimento WHERE IdPedidoPalm = @IDPEDIDO)\n" +
                " BEGIN\n" +
                "\t\tDECLARE @IDMOV INT\n" +
                "\t\tDECLARE XCURSOR CURSOR FOR \n" +
                "\t\tSELECT IdMov FROM Movimento m WHERE m.IdPedidoPalm = @IDPEDIDO\n" +
                "\t\tOPEN XCURSOR\n" +
                "\t\tFETCH NEXT FROM XCURSOR INTO @IDMOV\n" +
                "\t\tWHILE @@FETCH_STATUS = 0\n" +
                "\t\tBEGIN\n" +
                "\n" +
                "\t\t\tUPDATE Movimento SET IdPedidoPalm = NULL WHERE IdMov = @IDMOV\n" +
                "\t\t\n" +
                "\t\tFETCH NEXT FROM XCURSOR INTO @IDMOV\n" +
                "\t\tEND\n" +
                "\n" +
                "\t\tCLOSE XCURSOR\n" +
                "\t\tDEALLOCATE XCURSOR\n" +
                "\n" +
                " END\n" +
                " \n" +
                " UPDATE PedidoPalm set SituacaoPedido = 'P' WHERE IdPedidoPalm = @IDPEDIDO\n" +
                " DELETE PedidoPalm WHERE IdPedidoPalm = @IDPEDIDO"
        jdbcTemplate.update(query,  MapSqlParameterSource("numPedido", numPedido))
    }
    override fun findItems(numPedidoCronos: String, cancelados: Boolean): List<OrderItem> {
        val mapper = RowMapper<OrderItem> { rs: ResultSet, _: Int ->
            OrderItem().apply {
                val valorProduto = rs.getBigDecimal("LineNetAmount")
                val percentualDesconto = rs.getBigDecimal("LineNetAmount")
                val percentualDecimal = percentualDesconto.divide(BigDecimal.valueOf(100))
                val valorADeduzir = valorProduto.multiply(percentualDecimal)

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
                finalUnitPrice = valorProduto.subtract(valorADeduzir).toDouble()

            }
        }
        var query = "" +
                "DECLARE @PEDIDO VARCHAR(50)\n" +
                "\n" +
                "SET @PEDIDO = :numpedido\n" +
                "IF EXISTS (SELECT 1 FROM dbo.sym_petronas_order_item WHERE ERPOrderNumber = @PEDIDO and SFAOrderNumber is not null)\n" +
                "\tBEGIN\n" +
                "\t\tSELECT * FROM dbo.sym_petronas_order_item WHERE ERPOrderNumber = @PEDIDO and OrderItemStatus = 'FATURADO' \n" +
                "\tEND\n" +
                "ELSE\n" +
                "\tBEGIN\n" +
                "\tSELECT * FROM dbo.sym_petronas_order_item WHERE ERPOrderNumber = @PEDIDO AND Manufacturer = 'PETRONAS' and OrderItemStatus = 'FATURADO' \n" +
                "\tEND"
        if (cancelados) {
            query.replace("FATURADO", "CANCELADO")
        }

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
        val query = "DECLARE @IDMOV INT, @TIPOMOV VARCHAR(5), @IDPEDIDO INT\n" +
                "SELECT TOP 1 @IDMOV = IdMov, @TIPOMOV = TipoMov, @IDPEDIDO = IdPedidoPalm FROM Movimento WHERE NUMMOV = :numpedido AND TipoMov IN ('2.1', '2.4', '2.7')\n" +
                "IF @TIPOMOV IN ('2.7')\n" +
                "\tBEGIN\n" +
                "\tDECLARE @ID INT\n" +
                "        \n" +
                "\tDECLARE XCURSOR CURSOR FOR \n" +
                "\t\t\n" +
                "\t\tSELECT IdMov FROM Movimento WHERE IdPedidoPalm = @IDPEDIDO\n" +
                "\t\t\n" +
                "\tOPEN XCURSOR\n" +
                "\tFETCH NEXT FROM XCURSOR INTO @ID\n" +
                "\t\n" +
                "\tWHILE @@FETCH_STATUS = 0\n" +
                "\tBEGIN\n" +
                "\t\tUPDATE Movimento SET NumPrisma = 0 WHERE IdMov = @ID\n" +
                "\n" +
                "\tFETCH NEXT FROM XCURSOR INTO @ID\n" +
                "\tEND\n" +
                "\t\n" +
                "\tCLOSE XCURSOR\n" +
                "\tDEALLOCATE XCURSOR\n" +
                "\tEND\n" +
                "ELSE\n" +
                "\tBEGIN\n" +
                "\t\tUPDATE Movimento SET NumPrisma = 0 WHERE IdMov = @IDMOV\n" +
                "\tEND\n"
        jdbcTemplate.update(
            query,
            MapSqlParameterSource("numpedido", numPedidoCronos)
        )
    }

    override fun findAll(enviados: Boolean): List<Order> {

        return jdbcTemplate.query("SELECT * FROM sym_petronas_order WHERE PrecisaEnviar = 1", mapperOrder)
    }

    override fun findAll(initialDate: LocalDate?, endDate: LocalDate?, erpOrderNumber: String?): List<Order> {
        if(erpOrderNumber != null) {
            return jdbcTemplate.query("SELECT * FROM sym_petronas_order WHERE ERPOrderNumber = :numpedido", MapSqlParameterSource("numpedido", erpOrderNumber), mapperOrder)
        }
        if (initialDate != null && endDate != null) {
            return jdbcTemplate.query("SELECT * FROM sym_petronas_order WHERE DataErp BETWEEN :initialDate AND :endDate",
                MapSqlParameterSource("initialDate", initialDate).addValue("endDate", endDate), mapperOrder)
        }
        return jdbcTemplate.query("SELECT * FROM sym_petronas_order WHERE PrecisaEnviar = 1", mapperOrder)
    }


    override fun save(pedido: PedidoPalmPetronas): PedidoPalmPetronas {

        val queryInsertPedido = "" +
                "DECLARE @CODCOND VARCHAR(2), @CODPORTADOR VARCHAR(2)\n" +
                "SET @CODCOND = (SELECT TOP 1 ISNULL(CodCondPag,'01') FROM CondPag WHERE CondPag = :codcondicao)\n" +
                "SET @CODPORTADOR = (SELECT TOP 1 ISNULL(CodPortador,'01') FROM Portador WHERE NomePortador = :codportador)\n" +
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

        val query = "exec [dbo].[sym_insere_item_petronas] \n" +
                "   :numpedido\n" +
                "  ,:codfilial\n" +
                "  ,:dscretorno\n" +
                "  ,:codproduto\n" +
                "  ,:sequencia\n" +
                "  ,:qtd\n" +
                "  ,:tabela\n" +
                "  ,:preco\n" +
                "  ,:percdesc\n" +
                "  ,:situacao\n" +
                "  ,:logimport\n" +
                "  ,:idusuario\n" +
                "  ,:data\n" +
                "  ,:codretorno"


        val params =
            MapSqlParameterSource("dscretorno", item.descricaoRetornoItem).addValue("numpedido", numeroPedidoPetronas)
                .addValue("codfilial", codigoFilial)
                .addValue("codproduto", item.codigoProdutoArquivo).addValue("qtd", item.quantidadeSolicitada)
                .addValue("sequencia", item.sequencialItem)
                .addValue("tabela", item.idPrecoTabela).addValue("preco", item.precoUnitario)
                .addValue("percdesc", item.percentualDesconto).addValue("situacao", item.situacaoItem)
                .addValue("logimport", item.logImportacao).addValue("idusuario", item.idUsuario)
                .addValue("data", item.dataOperacao)
                .addValue("codretorno", item.codigoRetornoItem)

        return jdbcTemplate.query(query, params, mapperItemPedidoPalmPetronas).first()
    }

    override fun toMovimento(pedido: PedidoPalmPetronas) {
        val query = "exec dbo.sym_converte_pedido :idpedido, :tipomov, :codlocal"
        logger.info(pedido.toString())
        val params =
            MapSqlParameterSource("idpedido", pedido.idPedidoPalm).addValue("tipomov", "2.7").addValue("codlocal", "01")
        val result = jdbcTemplate.queryForObject(query, params, String::class.java)
        if (!result.isNullOrBlank()) {
            try {
                val idmov: Int =   result.split("-")[1].toInt()
                if (idmov != 0) {
                    finalizaMovimento.finaliza(idmov)
                    val resultado =  finalizaMovimento.finaliza(idmov)
                    logger.info("Finalizacao do Movimento executada para o movimento: $idmov, seu status foi: $resultado")
                }
            } catch (e: Exception) {
                logger.error("Erro ao receber o retorno da conversao de pedido", e)
            }

//                jdbcTemplate.queryForObject(
//                "SELECT ISNULL(MAX(IdMov),0) FROM Movimento WHERE NumMov = :numero",
//                MapSqlParameterSource("numero", result),
//                Int::class.java
//            ) ?: 0

        }
    }

    override fun convertAll() {
        val pedidos = jdbcTemplate.query(
            "SELECT * FROM PedidoPalm WHERE SituacaoPedido = 'P' AND Origem = 'DiscoverySFA'",
            mapperPedidoPalmPetronas
        )
        logger.info("Foram encontrados ${pedidos.size} pedidos para serem convertidos em orcamento")
        for (pedido in pedidos) {
            try {
                logger.info("Convertendo pedido ${pedido.numeroPedido} da petronas")
                toMovimento(pedido)
            }
            catch (e: Exception) {
                logger.error("Erro ao converter pedido", e)
            }
        }
    }

    companion object {

        private val mapperOrder =  RowMapper<Order> { rs: ResultSet, rowNum: Int ->
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
                takenBy = rs.getString("TakenBy")
            }

        }

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
                dataPedido = rs.getString("DataPedido")
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