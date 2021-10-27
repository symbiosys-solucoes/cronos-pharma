package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos

import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.sql.ResultSet
import java.sql.SQLException

@Repository
class PedidoPalmRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate,
    private val itemPedidoPalmRepository: ItemPedidoPalmRepository
) {
    val logger = LoggerFactory.getLogger(PedidoPalmRepository::class.java)
    fun save(pedidoPalm: PedidoPalm): PedidoPalm{

        when(pedidoPalm.Origem) {
            "" -> {//to-do
            }
            else -> {
                val dadosPedido = getDadosPedido(pedidoPalm)
                if (dadosPedido != null) {
                    pedidoPalm.CodCliFor = dadosPedido.codClifor
                    pedidoPalm.CodVendedor = dadosPedido.codVendedor
                    pedidoPalm.CodPortador = dadosPedido.codPortador
                    pedidoPalm.CodCondPag = dadosPedido.codCondPag
                }
            }
        }

        val params = MapSqlParameterSource()
            .addValue("origem", pedidoPalm.Origem)
            .addValue("idEmpresa", pedidoPalm.IdEmpresa)
            .addValue("numPedidoPalm", pedidoPalm.NumPedidoPalm)
            .addValue("codVendedor", pedidoPalm.CodVendedor)
            .addValue("codCliFor", pedidoPalm.CodCliFor)
            .addValue("dataPedido", pedidoPalm.DataPedido)
            .addValue("codCondPag", pedidoPalm.CodCondPag)
            .addValue("codPortador",pedidoPalm.CodPortador)
            .addValue("valorTotal", pedidoPalm.TotalPedido)
            .addValue("situacao", pedidoPalm.SituacaoPedido)
            .addValue("idUsuario", pedidoPalm.IdUsuario)
            .addValue("dataOperacao",pedidoPalm.DataOperacao)
            .addValue("cnpj", pedidoPalm.CnpjCpfCliFor)
            .addValue("codFilial", pedidoPalm.CodFilial)

        val pedido = jdbcTemplate.query(sqlInsertPedidoPalm, params, mapperPedidoPalm).first() ?: null
        if (pedido == null) {
            throw SQLException("Não foi Possivel inserir o pedido")
        }
        pedido.itens = pedidoPalm.itens

        pedido.itens.map { itemPedidoPalmRepository.save(it,pedido) }
        insereTotalPedido(pedido)
        return pedido
    }

    private fun getDadosPedido(pedido: PedidoPalm): DadosPedido?{
        val params = MapSqlParameterSource()
            .addValue("cnpj", pedido.CnpjCpfCliFor)
            .addValue("origem", pedido.Origem)
        return jdbcTemplate.query(sqlDadosItem, params, mapperDadosPedido).first() ?: null
    }

    fun findById(id: Long):PedidoPalm?{

        val pedido = jdbcTemplate.query("select * from PedidoPalm where IdPedidoPalm = :id", MapSqlParameterSource().addValue("id", id), mapperPedidoPalm).first()
        pedido.itens = itemPedidoPalmRepository.findAllByIdPedido(pedido?.IdPedidoPalm ?:0 )

        return pedido
    }

    fun toMovimento(pedido: PedidoPalm): String? {
        if(pedido.IdPedidoPalm == null || pedido.SituacaoPedido != "P" ){
            return "0"
        }

        return jdbcTemplate.query(sqlToMovimento, MapSqlParameterSource().addValue("idPedidoPalm", pedido.IdPedidoPalm), mapperString).first()

    }

    fun updateNomeArquivoRetorno(nomeArquivo: String, id: Long) {
        jdbcTemplate.queryForObject(
            "UPDATE PedidoPalm set ArqRetPed = :nomeArquivo where IdPedidoPalm = :id " +
                    "SELECT ArqRetPed FROM PedidoPalm WHERE IdPedidoPalm = :id",
            MapSqlParameterSource("nomeArquivo", nomeArquivo).addValue("id", id),
            String::class.java,
        )
    }

    fun insereTotalPedido(pedido: PedidoPalm) {
        try {
            jdbcTemplate.queryForObject(
                "UPDATE PedidoPalm set TotalPedido = (select TotalPedido = SUM( (ISNULL(Qtd,0)  *  ISNULL(PrecoUnit, 0)) * (1 - (ISNULL(PercDescontoItem,0) * 0.01))  * (1 - (ISNULL(PercDesconto,0) * 0.01)) )\n" +
                        "from ItemPedidoPalm Inner Join PedidoPalm on PedidoPalm.IdPedidoPalm = ItemPedidoPalm.IdPedidoPalm \n" +
                        "where PedidoPalm.IdPedidoPalm=:id) WHERE IdPedidoPalm = :id \n" +
                        "SELECT TotalPedido FROM PedidoPalm WHERE IdPedidoPalm = :id",
                MapSqlParameterSource("id", pedido.IdPedidoPalm),
                BigDecimal::class.java
            )
        } catch (e: SQLException) {
            logger.error("Erro ao atualizar o valor do pedido numero: ${pedido.NumPedidoPalm} ")
        }
    }
    companion object{
        private val mapperDadosPedido = RowMapper<DadosPedido>{ rs: ResultSet, rowNum: Int ->
            DadosPedido(
                codClifor = rs.getString("CODCLIFOR"),
                codPortador = rs.getString("CODPORTADOR"),
                codCondPag = rs.getString("CODCONDPAG"),
                codVendedor = rs.getString("CODVENDEDOR")
            )

        }
        private val mapperPedidoPalm = RowMapper<PedidoPalm> { rs: ResultSet, rowNum: Int ->
            PedidoPalm(
                Origem = rs.getString("Origem"),
                IdEmpresa = rs.getLong("IdEmpresa"),
                NumPedidoPalm = rs.getString("NumPedidoPalm"),
                CodVendedor = rs.getString("CodVendedor"),
                CodCliFor = rs.getString("CodCliFor"),
                DataPedido = rs.getTimestamp("DataPedido").toLocalDateTime(),
                PercComissao = rs.getDouble("PercComissao"),
                CodCondPag = rs.getString("CodCondPag"),
                CodPortador = rs.getString("CodPortador"),
                PercDesconto = rs.getDouble("PercDesconto"),
                TotalPedido = rs.getBigDecimal("TotalPedido"),
                DataEntrega = rs.getTimestamp("DataEntrega")?.toLocalDateTime() ?: null,
                Observacoes = rs.getString("Observacoes"),
                IdExpedicao = rs.getLong("IdExpedicao"),
                DataProxVisita = rs.getTimestamp("DataProxVisita")?.toLocalDateTime() ?: null,
                SituacaoPedido = rs.getString("SituacaoPedido"),
                NumNF = rs.getString("NumNF"),
                LogImportacao = rs.getString("LogImportacao"),
                IdUsuario = rs.getString("IdUsuario"),
                DataOperacao = rs.getTimestamp("DataOperacao").toLocalDateTime(),
                StatusRetorno = rs.getString("StatusRetorno"),
                NumPedidoCRONOS = rs.getString("NumPedidoCRONOS"),
                CodRetorno = rs.getString("CodRetorno"),
                DscRetorno = rs.getString("DscRetorno"),
                CnpjCpfCliFor = rs.getString("CnpjCpfCliFor"),
                ArqRetPed = rs.getString("ArqRetPed"),
                ArqRetNF = rs.getString("ArqRetNF"),
                CodFilial = rs.getString("CodFilial"),
                ArqRet2Ped = rs.getString("ArqRet2Ped"),
                VerLayout = rs.getString("VerLayout"),
                NumPedidoPalmAux = rs.getString("NumPedidoPalmAux"),
                itens = mutableListOf(),
                IdPedidoPalm = rs.getLong("IdPedidoPalm")
            )
        }
        private val mapperString = RowMapper<String> { rs: ResultSet, rowNum: Int ->
            rs.getString("VALOR")
        }
        private val sqlDadosItem = "" +
                "\n" +
                "  DECLARE @CNPJ VARCHAR(14), @ORIGEM VARCHAR(20)\n" +
                "  SET @CNPJ = :cnpj\n" +
                "  SET @ORIGEM = :origem\n" +
                "            IF (@ORIGEM = 'EMS')\n" +
                "\t\t\t  BEGIN\n" +
                "              SELECT\n" +
                "              CODVENDEDOR = ISNULL(Codfunc,'00001'),\n" +
                "              CODCLIFOR = codclifor,\n" +
                "              CODCONDPAG = ISNULL(codcondpag,'01'),\n" +
                "              CODPORTADOR = ISNULL(codportador,'01')\n" +
                "              FROM CLI_FOR WHERE dbo.fn_LimpaStr(CPFCGCCLIFOR) = @CNPJ               \n" +
                "              END\n" +
                "            IF (@ORIGEM = 'CONSYS')\n" +
                "              BEGIN\n" +
                "\t\t\t  SELECT\n" +
                "              CODVENDEDOR = ISNULL(codfunc,'00001'),\n" +
                "              CODCLIFOR = codclifor,\n" +
                "              CODCONDPAG = ISNULL(codcondpag,'01'),\n" +
                "              CODPORTADOR = ISNULL(codportador,'01')              \n" +
                "\t\t\t  FROM CLI_FOR WHERE dbo.fn_LimpaStr(CPFCGCCLIFOR) = @CNPJ \n" +
                "              END"

        private const val sqlInsertPedidoPalm =
            "BEGIN TRANSACTION \n" +
                    "\n" +
                    " INSERT INTO  PedidoPalm ( Origem, IdEmpresa, NumPedidoPalm, CodVendedor, CodCliFor, DataPedido, CodCondPag, CodPortador, TotalPedido, SituacaoPedido, IdUsuario, DataOperacao, CnpjCpfCliFor, CodFilial)\n" +
                    "\tOUTPUT INSERTED.*\n" +
                    "\t\tSELECT \t\n" +
                    "\t\tORIGEM = :origem ,\n" +
                    "\t\tIDEMPRESA = :idEmpresa,\n" +
                    "\t\tNUMPEDIDOPALM = :numPedidoPalm, \n" +
                    "\t\tCODVENDEDOR = ISNULL(:codVendedor,'00001'),\n" +
                    "\t\tCODCLIFOR = :codCliFor,\n" +
                    "\t\tDATAPEDIDO = :dataPedido,\n" +
                    "\t\tCODCONDPAG = ISNULL(:codCondPag,'01'),\n" +
                    "\t\tCODPORTADOR = ISNULL(:codPortador,'01'),\n" +
                    "\t\tTOTAL = :valorTotal,\n" +
                    "\t\tSITUACAO = :situacao,\n" +
                    "\t\tIDUSUARIO = :idUsuario,\n" +
                    "\t\tDATAOPERACAO = :dataOperacao,\n" +
                    "\t\tCNPJ = :cnpj,\n" +
                    "\t\tFILIAL = :codFilial\n" +
                    "\n" +
                    "IF @@ERROR = 0\n" +
                    "COMMIT\n" +
                    "ELSE\n" +
                    "ROLLBACK\n" +
                    "RAISERROR('Nao foi possivel inserir os dados',1,1)\t\t"

    }

    private val sqlToMovimento = "" +
            "\n" +
            "            DECLARE\n" +
            "                     @IdPedidoPalm \tINT, @IdItemPedidoPalm INT,\n" +
            "                     @PrecoUnit \t\tMONEY,\n" +
            "                     @Qtd      \t\tNUMERIC(15,6),  @QtdSolic NUMERIC(15,6),\n" +
            "                     @SdoAtual    NUMERIC(15,6),\n" +
            "                           @IdMovNew  \t\tINT,\n" +
            "                           @Item\t  \t\tINT,\n" +
            "                           @IdItemMovNew  \t\tINT,  @NumeroMovNew VARCHAR(20),\n" +
            "                           @AceitaEstoqueNegativo   VARCHAR(1),  @ControleLote  VARCHAR(1),\n" +
            "                           @IdProduto\t \tINT,\n" +
            "                           @CodProduto\t \tVARCHAR(20),\n" +
            "                           @IDMOV \t\t\tINT,\n" +
            "                           @CODFILIAL\t \tVARCHAR(2), @CODLOCAL  VARCHAR(2), @IDEMPRESA INT,\n" +
            "                           @INDPRECOVENDA \t\tVARCHAR(1),\n" +
            "                           @SITUACAOITEMPEDIDO \tVARCHAR(1), @UnidItemMov  VARCHAR(4),\n" +
            "                           @PercComissaoItem        NUMERIC(6,2),\n" +
            "                           @PercDescontoItem        FLOAT\n" +
            "            SET @IDEMPRESA = 1            \n" +
            "\t\t\tSET @IdPedidoPalm = :idPedidoPalm\n" +
            "            \n" +
            "            SET @IdMovNew = 0\n" +
            "            IF NOT EXISTS( SELECT 1 FROM Movimento M (NOLOCK) WHERE TipoMov ='2.1' AND IdPedidoPalm = @IdPedidoPalm AND Status <> 'C')\n" +
            "            BEGIN\n" +
            "            \n" +
            "            \n" +
            "             SELECT @NumeroMovNew = sym_nextNumMov FROM ZFiliaisCompl WHERE CodFilial = '01'\n" +
            "             UPDATE ZFiliaisCompl SET sym_nextNumMov = CAST(sym_nextNumMov as BigInt) + 1\n" +
            "            \n" +
            "             EXEC @IdMovNew = dbo.sp_NextId 'Movimento'\n" +
            "             \n" +
            "             BEGIN TRANSACTION\n" +
            "            \n" +
            "             INSERT INTO MOVIMENTO (IDEMPRESA, CODFILIAL, CODLOCAL, IDMOV, TIPOMOV, NUMMOV, DTMOV, CODCLIFOR, CODCONDPAG, CODVENDEDOR, IdRegiao, PERCCOMISSAO, STATUS, PercDesconto, Observacoes, DataEntrega, DataOperacao, IdUsuario, IdPedidoPalm, IdExpedicao, NumMovAux )\n" +
            "             SELECT @IdEmpresa,\n" +
            "                    Vendedores.CODFILIAL,\n" +
            "                    Vendedores.CodLocal,\n" +
            "                     @IdMovNew,\n" +
            "                  '2.1',\n" +
            "                    CONVERT(VARCHAR(20), @NumeroMovNew),\n" +
            "                    PedidoPalm.DataPedido,\n" +
            "                    PedidoPalm.CODCLIFOR,\n" +
            "                    PedidoPalm.CODCONDPAG,\n" +
            "                    PedidoPalm.CODVENDEDOR,\n" +
            "                    Cli_For.IdRegiao,\n" +
            "                    Vendedores.ComissaoVendedor,\n" +
            "                    'T',\n" +
            "                    ISNULL(PedidoPalm.PercDesconto,0),\n" +
            "                    CONVERT(VARCHAR(400),'Portador: '+ISNULL(Portador.NomePortador,'')+'  /  '+'Obs: '+ISNULL(PedidoPalm.Observacoes,'')),\n" +
            "                    PedidoPalm.DataEntrega,\n" +
            "                    GETDATE(),\n" +
            "                    PedidoPalm.Origem,\n" +
            "                     PedidoPalm.IdPedidoPalm,\n" +
            "                    PedidoPalm.IdExpedicao,\n" +
            "                    PedidoPalm.NumNF\n" +
            "              FROM PedidoPalm LEFT JOIN Portador ON PedidoPalm.CodPortador = Portador.CodPortador, Cli_For, Vendedores\n" +
            "             WHERE PedidoPalm.CodCliFor = Cli_For.CodCliFor\n" +
            "            \n" +
            "               AND PedidoPalm.CodVendedor = Vendedores.CodVendedor AND PedidoPalm.CodFilial = Vendedores.CodFilial \n" +
            "               AND Vendedores.CodFilial IS NOT NULL AND Vendedores.CodLocal IS NOT NULL\n" +
            "               AND PedidoPalm.IdPedidoPalm   = @IdPedidoPalm                    \n" +
            "\n" +
            "            \n" +
            "             DECLARE xItens SCROLL CURSOR FOR\n" +
            "              SELECT Vendedores.CodFilial, Vendedores.CodLocal, IdItemPedidoPalm, Item, CodProduto, Qtd, IdPrecoTabela, PrecoUnit, ISNULL(PercDescontoItem,0)\n" +
            "                FROM ItemPedidoPalm, PedidoPalm, Vendedores\n" +
            "               WHERE ItemPedidoPalm.IdPedidoPalm  = PedidoPalm.IdPedidoPalm\n" +
            "                 AND PedidoPalm.IdPedidoPalm      = @IdPedidoPalm\n" +
            "                 AND PedidoPalm.CodVendedor = Vendedores.CodVendedor \n" +
            "                 AND PedidoPalm.CodFilial = Vendedores.CodFilial \n" +
            "                 AND Vendedores.CodFilial IS NOT NULL AND Vendedores.CodLocal IS NOT NULL\n" +
            "              \n" +
            "             FOR READ ONLY\n" +
            "            \n" +
            "            OPEN xItens\n" +
            "            FETCH FIRST FROM xItens\n" +
            "             INTO @CodFilial, @CodLocal,\n" +
            "                  @IdItemPedidoPalm,\n" +
            "                  @Item,\n" +
            "                  @CodProduto,\n" +
            "                  @Qtd,\n" +
            "                  @IndPrecoVenda,\n" +
            "                  @PrecoUnit,\n" +
            "                  @PercDescontoItem\n" +
            "            \n" +
            "            WHILE @@FETCH_STATUS = 0\n" +
            "            BEGIN\n" +
            "            \n" +
            "              SELECT @SituacaoItemPedido = 'C'\n" +
            "            \n" +
            "              SELECT @IdProduto = MAX(IdProduto),\n" +
            "                     @PercComissaoItem = ISNULL(MAX(ComissaoProduto),0),\n" +
            "                     @UnidItemMov  = MAX(Produtos.Unid),\n" +
            "                     @ControleLote = ISNULL(MAX(Produtos.ControleLote),'N')\n" +
            "                FROM Produtos\n" +
            "               WHERE CodProduto = @CodProduto\n" +
            "            \n" +
            "              -- Produto nao cadastrado\n" +
            "              IF @IdProduto IS NULL\n" +
            "                 SELECT @SituacaoItemPedido = 'I'\n" +
            "            \n" +
            "              SELECT @SdoAtual   = ISNULL(MAX(e.SdoAtual),0)\n" +
            "                FROM Estoque e\n" +
            "               WHERE e.Codfilial = @CodFilial\n" +
            "                 AND e.CodLocal  = @CodLocal\n" +
            "                 AND e.IdProduto = @IdProduto\n" +
            "            \n" +
            "              SELECT @AceitaEstoqueNegativo = ISNULL(MAX(Loc.AceitaEstoqueNegativo),'N')\n" +
            "                FROM LocalEstoque Loc\n" +
            "               WHERE Loc.IdEmpresa = @IdEmpresa\n" +
            "                 AND Loc.CodFilial = @CodFilial\n" +
            "                 AND Loc.CodLocal  = @CodLocal\n" +
            "            \n" +
            "            \n" +
            "               SET @QtdSolic = @Qtd\n" +
            "            \n" +
            "               IF @AceitaEstoqueNegativo = 'N'\n" +
            "                IF (@Qtd > @SdoAtual)\n" +
            "                BEGIN\n" +
            "                  IF @SdoAtual > 0\n" +
            "                    SELECT @Qtd = @SdoAtual\n" +
            "                  ELSE\n" +
            "                    SELECT @Qtd = 0\n" +
            "                  SELECT @SituacaoItemPedido = 'I'\n" +
            "                END\n" +
            "            \n" +
            "               IF @IdProduto IS NOT NULL AND NOT EXISTS (SELECT 1 FROM ItensMov im (NOLOCK) WHERE im.IdMov = @IdMovNew AND im.IdProduto = @IdProduto)\n" +
            "               BEGIN\n" +
            "            \n" +
            "                EXEC @IdItemMovNew = dbo.sp_NextId 'ItensMov'\n" +
            "            \n" +
            "                INSERT INTO ItensMov (IdEmpresa, IdItemMov, IDMOV, IdProduto, UnidItemMov,  QtdSolic, QTD, PRECOUNIT, IDPRECOTABELA, PercDescontoItem, PercComissaoItem, DATAOPERACAO, IDUSUARIO)\n" +
            "                VALUES(@IdEmpresa,\n" +
            "                       @IdItemMovNew,\n" +
            "                       @IdMovNew,\n" +
            "                       @IdProduto,\n" +
            "                       @UnidItemMov,\n" +
            "                       @QtdSolic,\n" +
            "                       dbo.fn_Fmt(@QTD,'Q'),\n" +
            "                       dbo.fn_Fmt(@PRECOUNIT,'P'),\n" +
            "                       @IndPrecoVenda,\n" +
            "                       @PercDescontoItem,\n" +
            "                       @PercComissaoItem,\n" +
            "                     GETDATE(),\n" +
            "                     'PALM'\n" +
            "                      )\n" +
            "            \n" +
            "                   IF @ControleLote = 'S'\n" +
            "                     EXEC dbo.sp_GeraItemLoteAuto @IdItemMovNew\n" +
            "            \n" +
            "               END\n" +
            "            \n" +
            "               UPDATE dbo.ItemPedidoPalm SET SituacaoItemPedido = @SituacaoItemPedido, QtdConfirmada = @Qtd\n" +
            "                WHERE IdItemPedidoPalm  = @IdItemPedidoPalm\n" +
            "              \n" +
            "            \n" +
            "              FETCH NEXT FROM xItens\n" +
            "               INTO @CodFilial, @CodLocal,\n" +
            "                    @IdItemPedidoPalm,\n" +
            "                    @Item,\n" +
            "                    @CodProduto,\n" +
            "                    @Qtd,\n" +
            "                    @IndPrecoVenda,\n" +
            "                    @PrecoUnit,\n" +
            "                    @PercDescontoItem\n" +
            "            \n" +
            "            END\n" +
            "            \n" +
            "            CLOSE xItens\n" +
            "            DEALLOCATE xItens\n" +
            "            \n" +
            "            -- Grava o pedido como C-Confirmado\n" +
            "            IF EXISTS (SELECT 1 FROM Movimento WHERE IdMov = @IdMovNew)\n" +
            "             UPDATE PedidoPalm SET SituacaoPedido = 'C', NumPedidoCRONOS = @NumeroMovNew\n" +
            "              WHERE IdPedidoPalm  = @IdPedidoPalm\n" +
            "            ELSE\n" +
            "             SET @IdMovNew = 0        \n" +
            "             SELECT VALOR = @NumeroMovNew\n" +
            "            END \n" +
            "            \n" +
            "\n" +
            "\t\t\tIF @@ERROR = 0\n" +
            "\t\t\tCOMMIT\n" +
            "\t\t\tELSE\n" +
            "\t\t\tROLLBACK\n" +
            "\t\t\tRAISERROR('Nao foi possivel inserir os dados',1,1)        \n"

}

data class DadosPedido(
    val codClifor: String?,
    val codPortador: String?,
    val codVendedor: String?,
    val codCondPag: String?
)