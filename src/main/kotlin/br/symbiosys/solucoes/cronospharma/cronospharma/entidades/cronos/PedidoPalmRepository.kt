package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos

import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class PedidoPalmRepository(
    jdbcTemplate: NamedParameterJdbcTemplate
) {

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
            DataEntrega = rs.getTimestamp("DataEntrega").toLocalDateTime(),
            Observacoes = rs.getString("Observacoes"),
            IdExpedicao = rs.getLong("IdExpedicao"),
            DataProxVisita = rs.getTimestamp("DataProxVisita").toLocalDateTime(),
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

    private final val sqlFindCodCliforByCnpj = "" +
            "SELECT VALOR = CodCliFor FROM Cli_For where dbo.fn_LimpaStr(CPFCGCCLIFOR) = :cnpj"

    private final val sqlFindCodPortadorByCnpj = "" +
            "SELECT VALOR = CodPortador FROM Cli_For where dbo.fn_LimpaStr(CPFCGCCLIFOR) = :cnpj"

    private final val sqlFindCodVendedorByCnpj = "" +
            "SELECT VALOR = CodFunc FROM Cli_For where dbo.fn_LimpaStr(CPFCGCCLIFOR) = :cnpj"

    private final val sqlFindCodCondPagByCnpj = "" +
            "SELECT VALOR = CodCondPag FROM Cli_For where dbo.fn_LimpaStr(CPFCGCCLIFOR) = :cnpj"
    
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