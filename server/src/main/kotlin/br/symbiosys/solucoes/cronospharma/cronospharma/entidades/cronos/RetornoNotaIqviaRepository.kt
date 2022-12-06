package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.iqvia.RetornoNotaIqvia
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.sql.ResultSet

class RetornoNotaIqviaRepository (private val jdbcTemplate: NamedParameterJdbcTemplate, private val itensMovRepository: ItensMovRepository) {

    fun findRetornos(ids: List<Long>): List<RetornoNotaIqvia> {
        val retornos = mutableListOf<RetornoNotaIqvia>()
        ids.forEach {
            val result = jdbcTemplate.query(sqlFindPedido, MapSqlParameterSource("id", it), mapperPedido).first()
            val itens = itensMovRepository.findItemsNFByIdPedidoPalm(it)
            result.itens = itens
            retornos.add(result)
        }
        return retornos
    }

    companion object {
        val mapperPedido = RowMapper<RetornoNotaIqvia> { rs, _ ->
            mapperPedido(rs)
        }

        private fun mapperPedido(rs: ResultSet): RetornoNotaIqvia {
            val retorno = RetornoNotaIqvia()
            retorno.cnpjDistribuidor = rs.getString("CnpjFilial")
            retorno.numeroPedidoOl = rs.getString("NumPedidoPalm")
            retorno.dataSaidaMercadoria = rs.getTimestamp("DataEntradaSaida").toLocalDateTime()
            retorno.dataEmissaoNF = rs.getTimestamp("DtMov").toLocalDateTime().toLocalDate()
            retorno.cnpjCliente = rs.getString("CnpjCpfCliFor")
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

            return retorno
        }

        const val sqlFindPedido = "" +
                "DECLARE @IDPEDIDOPALM INT\n" +
                "SET @IDPEDIDOPALM = :id\n" +
                "\n" +
                "SELECT CnpjFilial = (SELECT dbo.fn_LimpaStr(CGC) FROM Filiais WHERE CodFilial = M.CodFilial), P.NumPedidoPalm,  M.DataEntradaSaida, M.DtMov, P.CnpjCpfCliFor, M.NumMov, \n" +
                "M.SerieDoc, Vencimento = ISNULL((Select MAX(CPR.DTVENCIMENTO) FROM CPR WHERE IdMov = M.IdMov), GETDATE()), M.ValorFrete, M.ValorSeguro, M.OutrasDespesas,\n" +
                "M.TotMov,\n" +
                "ValorTotalNF =  dbo.fn_ValorMovimento2( M.IdMov, M.TotMov, M.PercDesconto, M.ValorSubICMS, M.ValorFrete, M.ValorSeguro, M.OutrasDespesas, M.ValorIPI, M.Frete, 'L'),\n" +
                "M.ValorIPI, M.BaseICMS, M.ValorICMS, M.BaseSubICMS, ValorSubICMS\n" +
                "FROM PedidoPalm P INNER JOIN Movimento M ON p.IdPedidoPalm = M.IdPedidoPalm\n" +
                "WHERE P.IdPedidoPalm = @IDPEDIDOPALM\n" +
                "AND P.ArqRetPed IS NOT NULL\n" +
                "AND P.ArqRetNF IS NULL\n"

    }
}