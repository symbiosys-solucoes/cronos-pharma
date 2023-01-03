package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.ems.ItemRetornoNotaEMS
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.ems.RetornoNotaEMS
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.iqvia.ItemRetornoNotaIqvia
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.iqvia.RetornoNotaIqvia
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class RetornoNotaEMSRepository (private val jdbcTemplate: NamedParameterJdbcTemplate, private val itensMovRepository: ItensMovRepository) {

    fun findRetornos(ids: List<Long>, force: Boolean = false): List<RetornoNotaEMS> {
        val retornos = mutableListOf<RetornoNotaEMS>()
        ids.forEach { id ->
            val result = jdbcTemplate.query( if (force) sqlFindForced else sqlFindPedido  , MapSqlParameterSource("id", id), mapperPedido).first()
            val itens = itensMovRepository.findItemsNFByIdPedidoPalm(id)
            result.itens = itens.map { itemIqviatoItemEMS(it) }.toMutableList()
            retornos.add(result)
        }
        return retornos
    }

    private fun itemIqviatoItemEMS(itemIqvia: ItemRetornoNotaIqvia) : ItemRetornoNotaEMS {
        val itemEms = ItemRetornoNotaEMS()
        itemEms.codigoEANProduto = itemIqvia.codigoEAN
        itemEms.quantidadeAtendida = itemIqvia.quantidade
        itemEms.descontoAplicado = itemIqvia.descontoComercial
        return itemEms
    }

    companion object {
        val mapperPedido = RowMapper<RetornoNotaEMS> { rs, _ ->
            mapperPedido(rs)
        }

        private fun mapperPedido(rs: ResultSet): RetornoNotaEMS {
            val retorno = RetornoNotaEMS()
            retorno.cnpjFarmacia = rs.getString("CnpjCpfCliFor")
            retorno.numeroNotaFiscal = rs.getString("NumMov")
            retorno.numeroSerieNotaFiscal = rs.getString("SerieDoc")
            retorno.numeroPedidoIndustria = rs.getString("NumPedidoPalm")
            retorno.numeroPedidoOL = rs.getString("NumPedidoCRONOS")
            retorno.dataFaturamento = rs.getTimestamp("DataEntradaSaida").toLocalDateTime()
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
                "M.ValorIPI, M.BaseICMS, M.ValorICMS, M.BaseSubICMS, ValorSubICMS, P.NumPedidoCRONOS\n" +
                "FROM PedidoPalm P INNER JOIN Movimento M ON p.IdPedidoPalm = M.IdPedidoPalm\n" +
                "WHERE P.IdPedidoPalm = @IDPEDIDOPALM\n" +
                "AND P.ArqRetPed IS NOT NULL\n" +
                "AND P.ArqRetNF IS NULL\n"

        const val sqlFindForced = "" +
                "DECLARE @IDPEDIDOPALM INT\n" +
                "SET @IDPEDIDOPALM = :id\n" +
                "\n" +
                "SELECT CnpjFilial = (SELECT dbo.fn_LimpaStr(CGC) FROM Filiais WHERE CodFilial = M.CodFilial), P.NumPedidoPalm,  M.DataEntradaSaida, M.DtMov, P.CnpjCpfCliFor, M.NumMov, \n" +
                "M.SerieDoc, Vencimento = ISNULL((Select MAX(CPR.DTVENCIMENTO) FROM CPR WHERE IdMov = M.IdMov), GETDATE()), M.ValorFrete, M.ValorSeguro, M.OutrasDespesas,\n" +
                "M.TotMov,\n" +
                "ValorTotalNF =  dbo.fn_ValorMovimento2( M.IdMov, M.TotMov, M.PercDesconto, M.ValorSubICMS, M.ValorFrete, M.ValorSeguro, M.OutrasDespesas, M.ValorIPI, M.Frete, 'L'),\n" +
                "M.ValorIPI, M.BaseICMS, M.ValorICMS, M.BaseSubICMS, ValorSubICMS, P.NumPedidoCRONOS\n" +
                "FROM PedidoPalm P INNER JOIN Movimento M ON p.IdPedidoPalm = M.IdPedidoPalm\n" +
                "WHERE P.IdPedidoPalm = @IDPEDIDOPALM\n"


    }
}