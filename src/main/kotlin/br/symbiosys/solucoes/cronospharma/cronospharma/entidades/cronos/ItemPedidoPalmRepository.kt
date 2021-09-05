package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos

import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.sql.ResultSet

@Repository
class ItemPedidoPalmRepository (
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {
    private val logger = LoggerFactory.getLogger(ItemPedidoPalmRepository::class.java)

    fun save(item: ItemPedidoPalm, pedido: PedidoPalm): ItemPedidoPalm{
            val params = MapSqlParameterSource()
                .addValue("idPedidoPalm",item.IdPedidoPalm)
                .addValue("item", item.Item)
                .addValue("idEmpresa",item.IdEmpresa)
                .addValue("idProduto", item.IdProduto)
                .addValue("codProduto", item.CodProduto)
                .addValue("qtd", item.Qtd)
                .addValue("qtdConfirmada", item.QtdConfirmada)
                .addValue("idPrecoTabela", item.IdPrecoTabela)
                .addValue("precoUnit", item.PrecoUnit)
                .addValue("percDesc", item.PercDescontoItem)
                .addValue("percComissao", item.PercComissaoItem)
                .addValue("situacaoItem", item.SituacaoItemPedido)
                .addValue("logImportacao", item.LogImportacao)
                .addValue("idUsuario", item.IdUsuario)
                .addValue("dataOperacao", item.DataOperacao)
                .addValue("codRetorno", item.CodRetornoItem)
                .addValue("dscRetorno", item.DscRetornoItem)
                .addValue("codProdutoArq", item.CodProdutoArq)

        when(pedido.Origem){
            "EMS" -> {
                item.IdPedidoPalm = pedido.IdPedidoPalm
                item.IdProduto = jdbcTemplate.query(findIdProdutoByCodbarra, MapSqlParameterSource().addValue("codigoBarra", item.CodProdutoArq), mapperString).first().toInt() ?: 0
                item.CodProduto = jdbcTemplate.query(findCodprodutoByCodBarra, MapSqlParameterSource().addValue("codigoBarra", item.CodProdutoArq), mapperString).first() ?: "0"
                item.PrecoUnit = jdbcTemplate.query(findPrecoVenda1ByCodBarra, MapSqlParameterSource().addValue("codigoBarra", item.CodProdutoArq), mapperString).first().toBigDecimal() ?: BigDecimal("0")
            }
            "CONSYS" -> {
                item.IdPedidoPalm = pedido.IdPedidoPalm
                item.IdProduto = jdbcTemplate.query(findIdProdutoByCodbarra, MapSqlParameterSource().addValue("codigoBarra", item.CodProdutoArq), mapperString).first().toInt() ?: 0
                item.CodProduto = jdbcTemplate.query(findCodprodutoByCodBarra, MapSqlParameterSource().addValue("codigoBarra", item.CodProdutoArq), mapperString).first() ?: "0"
                item.IdPrecoTabela = jdbcTemplate.query(findIdPrecoByCnpj, MapSqlParameterSource().addValue("cnpj", pedido.CnpjCpfCliFor), mapperString).first() ?: "1"
                item.PrecoUnit = jdbcTemplate.query(findPrecoUnitByCodCondAndIdPreco, MapSqlParameterSource().addValue("codCondPag", pedido.CodCondPag).addValue("idPreco",item.IdPrecoTabela), mapperString).first().toBigDecimal() ?: BigDecimal("0")
            }
        }

        val item = jdbcTemplate.query(sqlInsertItemPedidoPalm, params, mapperItemPedidoPalm).first()


        return item
    }

    private final val mapperString = RowMapper<String> { rs: ResultSet, rowNum: Int ->
        rs.getString(1)
    }

    private final val findIdPrecoByCnpj = "" +
            "select top 1 BuscarPreco from Cli_For where CPFCGCCLIFOR = :cnpj and CodCliFor like 'C%'"

    private final val findPrecoUnitByCodCondAndIdPreco = "" +
            "DECLARE @CODCONDPAG VARCHAR(10), @IDPRECO varchar(1)\n" +
            "\n" +
            "set @CODCONDPAG = :codCondPag\n" +
            "set @IDPRECO = :idPreco\n" +
            "\n" +
            "SELECT \n" +
            "  PRECOUNIT = CASE WHEN IDPRODUTO IN ( SELECT MAX(IDPRODUTO) FROM CondPagRegraCP CP WHERE CP.CodCondPag = @CODCONDPAG AND CP.RegraAtiva = 'S') THEN\n" +
            "  ( SELECT MAX(IDPRODUTO) FROM CondPagRegraCP CP WHERE CP.CodCondPag = @CODCONDPAG AND CP.RegraAtiva = 'S')\n" +
            "  ELSE CASE WHEN @IDPRECO = 1 THEN (SELECT MAX(PRECOVENDA1) FROM Produtos WHERE IDPRODUTO=IDPRODUTO )\n" +
            "  WHEN @IDPRECO = 2 THEN (SELECT MAX(PrecoVenda2) FROM Produtos WHERE IDPRODUTO=IDPRODUTO )  \n" +
            "  WHEN @IDPRECO = 3 THEN (SELECT MAX(PrecoVenda3) FROM Produtos WHERE IDPRODUTO=IDPRODUTO ) \n" +
            "  WHEN @IDPRECO = 4 THEN (SELECT MAX(PrecoVenda4) FROM Produtos WHERE IDPRODUTO=IDPRODUTO ) \n" +
            "  WHEN @IDPRECO = 5 THEN (SELECT MAX(PrecoVenda5) FROM Produtos WHERE IDPRODUTO=IDPRODUTO ) \n" +
            "  WHEN @IDPRECO = 6 THEN (SELECT MAX(PrecoVenda6) FROM Produtos WHERE IDPRODUTO=IDPRODUTO ) ELSE \n" +
            "  (SELECT MAX(PRECOVENDA1) FROM Produtos WHERE IDPRODUTO=IDPRODUTO ) END END\n" +
            "\n" +
            "FROM PRODUTOS"

    private final val findPrecoVenda1ByCodBarra = "" +
            "SELECT PrecoVenda1 FROM .Produtos WHERE IDPRODUTO = (SELECT IDPRODUTO FROM CodigoBarras WHERE CodigoBarras = :codigoBarra) "

    private final val findCodprodutoByCodBarra = "" +
            "select CodProduto from Produtos where IdProduto = (select top 1 IdProduto from CodigoBarras where CodigoBarras = :codigoBarra)"

    private final val findIdProdutoByCodbarra =  "" +
            "select top 1 IdProduto from CodigoBarras where CodigoBarras = :codigoBarra"

    private final val mapperItemPedidoPalm = RowMapper<ItemPedidoPalm> { rs: ResultSet, rowNum: Int ->
        ItemPedidoPalm(
            IdPedidoPalm = rs.getLong("IdPedidoPalm"),
            Item = rs.getInt("Item"),
            IdEmpresa = rs.getLong("IdEmpresa"),
            CodProduto = rs.getString("CodProduto"),
            CodProdutoArq = rs.getString("CodProdutoArq"),
            IdProduto = rs.getInt("IdProduto"),
            Qtd = rs.getDouble("Qtd"),
            QtdConfirmada = rs.getDouble("QtdConfirmada"),
            IdPrecoTabela = rs.getString("IdPrecoTabela"),
            PrecoUnit = rs.getBigDecimal("PrecoUnit"),
            PercComissaoItem = rs.getDouble("PercComissaoItem"),
            PercDescontoItem = rs.getDouble("PercDescontoItem"),
            SituacaoItemPedido = rs.getString("SituacaoItemPedido"),
            LogImportacao = rs.getString("LogImportacao"),
            CodRetornoItem = rs.getString("CodRetornoItem"),
            DscRetornoItem = rs.getString("DscRetornoItem"),
            IdUsuario = rs.getString("IdUsuario"),
            DataOperacao = rs.getTimestamp("DataOperacao").toLocalDateTime(),
            IdItemPedidoPalm = rs.getLong("IdItemPedidoPalm")

        )
    }
    private final val sqlInsertItemPedidoPalm = "" +
            "\n" +
            "BEGIN TRANSACTION\n" +
            "\n" +
            "\tINSERT INTO ITEMPEDIDOPALM (IDPEDIDOPALM,ITEM,IDEMPRESA,IDPRODUTO,CODPRODUTO,QTD,QtdConfirmada,IDPRECOTABELA,PRECOUNIT,PERCDESCONTOITEM,PercComissaoItem,SituacaoItemPedido,LogImportacao,IdUsuario,DataOperacao,CodRetornoItem,DscRetornoItem,CodProdutoArq)\n" +
            "              \n" +
            "\tOUTPUT INSERTED.*\n" +
            "     \n" +
            "\t\tSELECT \n" +
            "\t\tIDPEDIDOPALM = :idPedidoPalm,\n" +
            "\t\tITEM = :item,\n" +
            "\t\tIDEMPRESA = :idEmpresa,\n" +
            "\t\tIDPRODUTO = :idProduto,\n" +
            "\t\tCODPRODUTO = :codProduto,\n" +
            "\t\tQTD = :qtd,\n" +
            "\t\tQTDCONFIRMADA = :qtdConfirmada,\n" +
            "\t\tIDPRECOTABELA = :idPrecoTabela,\n" +
            "\t\tPRECOUNIT = :precoUnit,\n" +
            "\t\tPERCDESCONTOITEM = :percDesc,\n" +
            "\t\tPERCCOMISSAOITEM = :percComissao,\n" +
            "\t\tSITUACAOITEMPEDIDO = :situacaoItem,\n" +
            "\t\tLOGIMPORTACAO = :logImportacao,\n" +
            "\t\tIDUSUARIO = :idUsuario,\n" +
            "\t\tDATAOPERACAO = :dataOperacao,\n" +
            "\t\tCODRETORNOITEM = :codRetorno,\n" +
            "\t\tDSCRETORNOITEM = :dscRetorno,\n" +
            "\t\tCODPRODUTOARQ = :codProdutoArq\n" +
            "\n" +
            "IF @@ERROR = 0\n" +
            "COMMIT\n" +
            "ELSE\n" +
            "ROLLBACK\n" +
            "RAISERROR('Nao foi possivel inserir os dados',1,1)\t"

    private final val sqlDadosItem = "" +
            "DECLARE @CODIGOBARRAS VARCHAR(14), @ORIGEM VARCHAR(20), @CODCOND VARCHAR(20), @CNPJ Varchar(20)\n" +
            "SET @CODIGOBARRAS = :codigoBarras\n" +
            "SET @ORIGEM = :origem\n" +
            "SET @CODCOND = ISNULL(:codCond,1)\n" +
            "SET @CNPJ = :cnpj\n" +
            "\n" +
            "\n" +
            "iF @ORIGEM = 'EMS'\n" +
            "BEGIN\n" +
            "  SELECT \n" +
            "  IDPRODUTO = (SELECT IDPRODUTO FROM CodigoBarras WHERE CodigoBarras = @CODIGOBARRAS),\n" +
            "  CODPRODUTO = (SELECT CODPRODUTO FROM Produtos WHERE IDPRODUTO = (SELECT IDPRODUTO FROM.CodigoBarras WHERE CodigoBarras = @CODIGOBARRAS) ),        \n" +
            "  IDPRECOTABELA = 1,\n" +
            "  PRECOUNIT = (SELECT PrecoVenda1 FROM Produtos WHERE IDPRODUTO = (SELECT IDPRODUTO FROM CodigoBarras WHERE CodigoBarras = @CODIGOBARRAS) )\n" +
            "\t\t\t  \n" +
            "  FROM Produtos\n" +
            "  WHERE IdProduto = (select top 1 idproduto from CodigoBarras where CodigoBarras = @CODIGOBARRAS )\n" +
            "END\n" +
            "IF @ORIGEM = 'CONSYS'\n" +
            "BEGIN\n" +
            "\tDECLARE @IDPRECO varchar(1), @IDPRODUTO int\n" +
            "\tSET @IDPRODUTO = (SELECT IDPRODUTO FROM CodigoBarras WHERE CodigoBarras = @CODIGOBARRAS)\n" +
            "\tset @IDPRECO = ISNULL((SELECT MAX(BuscarPreco) FROM Cli_For where dbo.fn_LimpaStr(CPFCGCCLIFOR) = @CNPJ and CodCliFor like 'C%'),1)\n" +
            "\n" +
            "\tSELECT \n" +
            "     IDPRODUTO = @IDPRODUTO,\n" +
            "\t CODPRODUTO = (SELECT CODPRODUTO FROM Produtos WHERE IDPRODUTO = (SELECT IDPRODUTO FROM.CodigoBarras WHERE CodigoBarras = @CODIGOBARRAS) ),\n" +
            "\t IDPRECOTABELA = @IDPRECO,\n" +
            "              PRECOUNIT = CASE WHEN IDPRODUTO IN ( SELECT MAX(IDPRODUTO) FROM CondPagRegraCP CP WHERE CP.CodCondPag = @CODCOND AND CP.RegraAtiva = 'S') THEN\n" +
            "                    ( SELECT MAX(IDPRODUTO) FROM CondPagRegraCP CP WHERE CP.CodCondPag = @CODCOND AND CP.RegraAtiva = 'S')\n" +
            "                    ELSE CASE WHEN @IDPRECO = 1 THEN (SELECT MAX(PRECOVENDA1) FROM Produtos WHERE IDPRODUTO=@IDPRODUTO )\n" +
            "                    WHEN @IDPRECO = 2 THEN (SELECT MAX(PrecoVenda2) FROM Produtos WHERE IDPRODUTO=@IDPRODUTO )  \n" +
            "                    WHEN @IDPRECO = 3 THEN (SELECT MAX(PrecoVenda3) FROM Produtos WHERE IDPRODUTO=@IDPRODUTO ) \n" +
            "                    WHEN @IDPRECO = 4 THEN (SELECT MAX(PrecoVenda4) FROM Produtos WHERE IDPRODUTO=@IDPRODUTO ) \n" +
            "                    WHEN @IDPRECO = 5 THEN (SELECT MAX(PrecoVenda5) FROM Produtos WHERE IDPRODUTO=@IDPRODUTO ) \n" +
            "                    WHEN @IDPRECO = 6 THEN (SELECT MAX(PrecoVenda6) FROM Produtos WHERE IDPRODUTO=@IDPRODUTO ) ELSE \n" +
            "                    (SELECT MAX(PRECOVENDA1) FROM Produtos WHERE IDPRODUTO=@IDPRODUTO ) END END\n" +
            "\tFROM PRODUTOS where IdProduto = @IDPRODUTO"
}

data class DadosItem(
    var idProduto:String?,
    var codProduto:String?,
    var IdPrecoTabela: String?,
    var PrecoUnit: BigDecimal?
)
{}