package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.closeup.NotaFiscal
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class RetornoNotaCLOSEUPRepository (private val jdbcTemplate: NamedParameterJdbcTemplate, private val itensMovRepository: ItensMovRepository) {

    fun findRetornos(ids: List<Long>, force: Boolean = false): List<NotaFiscal> {
        val retornos = mutableListOf<NotaFiscal>()
        ids.forEach {
            val result = jdbcTemplate.query( if (force) sqlFindForced else sqlFindPedido  , MapSqlParameterSource("id", it), mapperPedido).first()
            val itens = itensMovRepository.findItemsNFByIdPedidoPalmCloseUP(it)
            result.items = itens
            retornos.add(result)
        }
        return retornos
    }

    companion object {
        val mapperPedido = RowMapper<NotaFiscal> { rs, _ ->
            mapperPedido(rs)
        }

        private fun mapperPedido(rs: ResultSet): NotaFiscal {
            val retorno = NotaFiscal()
            retorno.cnpjDistribuidor = rs.getString("CnpjFilial")
            retorno.numeroPedidoMedquimica = rs.getString("NumPedidoPalm")
            retorno.dataFaturamento = rs.getTimestamp("DataEntradaSaida").toLocalDateTime()
            retorno.dataEmissaoNotaFiscal = rs.getTimestamp("DtMov").toLocalDateTime()
            retorno.cnpjFarmacia = rs.getString("CnpjCpfCliFor")
            retorno.numeroNotaFiscal = rs.getString("NumMov")
            retorno.serieNF = rs.getString("SerieDoc")
            retorno.vencimentoFatura = rs.getTimestamp("Vencimento").toLocalDateTime().toLocalDate()
            retorno.valorFrete = rs.getDouble("ValorFrete")
            retorno.valorSeguro = rs.getDouble("ValorSeguro")
            retorno.valorDespesas = rs.getDouble("OutrasDespesas")
            retorno.valorTotalProdutos = rs.getDouble("TotMov")
            retorno.valorTotalNF = rs.getDouble("ValorTotalNF")
            retorno.valorIPI = rs.getDouble("ValorIPI")
            retorno.baseCalculoIcms = rs.getDouble("BaseICMS")
            retorno.valorIcms = rs.getDouble("ValorICMS")
            retorno.baseCalculoSubstituicaoTributaria = rs.getDouble("BaseSubICMS")
            retorno.valorIcmsSubstituicaoTributaria = rs.getDouble("ValorSubICMS")
            retorno.chaveNotaFiscal = rs.getString("ChaveNFe,")

            return retorno
        }

        const val sqlFindPedido = "" +
                "DECLARE @IDPEDIDOPALM INT\n" +
                "SET @IDPEDIDOPALM = :id\n" +
                "\n" +
                "SELECT M.ChaveNFe, CnpjFilial = (SELECT dbo.fn_LimpaStr(CGC) FROM Filiais WHERE CodFilial = M.CodFilial), P.NumPedidoPalm,  M.DataEntradaSaida, M.DtMov, P.CnpjCpfCliFor, M.NumMov, \n" +
                "M.SerieDoc, Vencimento = ISNULL((Select MAX(CPR.DTVENCIMENTO) FROM CPR WHERE IdMov = M.IdMov), GETDATE()), M.ValorFrete, M.ValorSeguro, M.OutrasDespesas,\n" +
                "M.TotMov,\n" +
                "ValorTotalNF =  dbo.fn_ValorMovimento2( M.IdMov, M.TotMov, M.PercDesconto, M.ValorSubICMS, M.ValorFrete, M.ValorSeguro, M.OutrasDespesas, M.ValorIPI, M.Frete, 'L'),\n" +
                "M.ValorIPI, M.BaseICMS, M.ValorICMS, M.BaseSubICMS, ValorSubICMS\n" +
                "FROM PedidoPalm P INNER JOIN Movimento M ON p.IdPedidoPalm = M.IdPedidoPalm\n" +
                "WHERE P.IdPedidoPalm = @IDPEDIDOPALM\n" +
                "AND P.ArqRetPed IS NOT NULL\n" +
                "AND P.ArqRetNF IS NULL\n"

        const val sqlFindForced = "" +
                "DECLARE @IDPEDIDOPALM INT\n" +
                "SET @IDPEDIDOPALM = :id\n" +
                "\n" +
                "SELECT M.ChaveNFe, CnpjFilial = (SELECT dbo.fn_LimpaStr(CGC) FROM Filiais WHERE CodFilial = M.CodFilial), P.NumPedidoPalm,  M.DataEntradaSaida, M.DtMov, P.CnpjCpfCliFor, M.NumMov, \n" +
                "M.SerieDoc, Vencimento = ISNULL((Select MAX(CPR.DTVENCIMENTO) FROM CPR WHERE IdMov = M.IdMov), GETDATE()), M.ValorFrete, M.ValorSeguro, M.OutrasDespesas,\n" +
                "M.TotMov,\n" +
                "ValorTotalNF =  dbo.fn_ValorMovimento2( M.IdMov, M.TotMov, M.PercDesconto, M.ValorSubICMS, M.ValorFrete, M.ValorSeguro, M.OutrasDespesas, M.ValorIPI, M.Frete, 'L'),\n" +
                "M.ValorIPI, M.BaseICMS, M.ValorICMS, M.BaseSubICMS, ValorSubICMS\n" +
                "FROM PedidoPalm P INNER JOIN Movimento M ON p.IdPedidoPalm = M.IdPedidoPalm\n" +
                "WHERE P.IdPedidoPalm = @IDPEDIDOPALM\n"


    }
}