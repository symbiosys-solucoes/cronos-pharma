package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos

import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.sql.SQLException

@Repository
class PedidoPalmRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate,
    private val itemPedidoPalmRepository: ItemPedidoPalmRepository
) {
    fun save(pedidoPalm: PedidoPalm): PedidoPalm{

        when(pedidoPalm.Origem) {
            "EMS" -> {
                val dadosPedido = getDadosPedido(pedidoPalm)
                if (dadosPedido != null) {
                    pedidoPalm.CodCliFor = dadosPedido.codClifor
                    pedidoPalm.CodVendedor = dadosPedido.codVendedor
                    pedidoPalm.CodPortador = dadosPedido.codPortador
                    pedidoPalm.CodCondPag = dadosPedido.codCondPag
                }

            }
            "CONSYS" -> {
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

        return pedido
    }

    private fun getDadosPedido(pedido: PedidoPalm): DadosPedido?{
        val params = MapSqlParameterSource()
            .addValue("cnpj", pedido.CnpjCpfCliFor)
            .addValue("origem", pedido.Origem)
        return jdbcTemplate.query(sqlDadosItem, params, mapperDadosPedido).first() ?: null
    }

    companion object{
        private final val mapperDadosPedido = RowMapper<DadosPedido>{ rs: ResultSet, rowNum: Int ->
            DadosPedido(
                codClifor = rs.getString("CODCLIFOR"),
                codPortador = rs.getString("CODPORTADOR"),
                codCondPag = rs.getString("CODCONDPAG"),
                codVendedor = rs.getString("CODVENDEDOR")
            )

        }
        private final val mapperPedidoPalm = RowMapper<PedidoPalm> {rs: ResultSet, rowNum: Int ->
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
        private final val mapperString = RowMapper<String> { rs: ResultSet, rowNum: Int ->
            rs.getString("VALOR")
        }
        private final val sqlDadosItem = "" +
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

        private final val sqlInsertPedidoPalm =
            "BEGIN TRANSACTION \n" +
                    "\n" +
                    " INSERT INTO  PedidoPalm ( Origem, IdEmpresa, NumPedidoPalm, CodVendedor, CodCliFor, DataPedido, CodCondPag, CodPortador, TotalPedido, SituacaoPedido, IdUsuario, DataOperacao, CnpjCpfCliFor, CodFilial)\n" +
                    "              \n" +
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

}

data class DadosPedido(
    val codClifor: String?,
    val codPortador: String?,
    val codVendedor: String?,
    val codCondPag: String?
)