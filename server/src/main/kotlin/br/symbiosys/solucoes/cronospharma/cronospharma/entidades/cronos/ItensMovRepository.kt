package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.iqvia.ItemRetornoNotaIqvia
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.closeup.ItemNotaFiscal
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class ItensMovRepository
    (private val jdbcTemplate: NamedParameterJdbcTemplate)
{

    fun findItemsNFByIdPedidoPalm(idPedido: Long): MutableList<ItemRetornoNotaIqvia> {
        return jdbcTemplate.query(findItensNotaByIdPedidoPalm, MapSqlParameterSource("id", idPedido), mapperItem)
    }

    fun findItemsNFByIdPedidoPalmCloseUP(idPedido: Long): MutableList<ItemNotaFiscal> {
        return jdbcTemplate.query(findItensNotaByIdPedidoPalm, MapSqlParameterSource("id", idPedido), mapperCloseUpItem)
    }
    companion object {

        private val mapperCloseUpItem = RowMapper<ItemNotaFiscal> { rs: ResultSet, _: Int ->
            returnItemCloseUp(rs)
        }

        private fun returnItemCloseUp(rs: ResultSet): ItemNotaFiscal {
            val item = ItemNotaFiscal()
            item.codigoBarras = rs.getString("CodProdutoArq")
            item.quantidadeAtendida = rs.getDouble("Qtd").toInt()
            item.valorLiquidoNotaFiscal = rs.getDouble("PrecoUnitLiq")
            item.descontoAplicado = rs.getDouble("ValorDesconto")
            item.valorSubstituicaoTributaria = rs.getDouble("ValorItemSubICMS")

            return item
        }


        private val mapperItem = RowMapper<ItemRetornoNotaIqvia> { rs: ResultSet, _: Int ->
            returnItem(rs)
        }

        private fun returnItem(rs: ResultSet): ItemRetornoNotaIqvia {
            val item = ItemRetornoNotaIqvia()
            item.codigoEAN = rs.getString("CodProdutoArq")
            item.codigoProdutoDistribuidor = rs.getString("CodProduto")
            item.quantidade = rs.getDouble("Qtd")
            item.tipoEmbalagem = rs.getString("UnidItemMov")
            item.preco = rs.getDouble("PrecoUnitLiq")
            item.descontoComercial = rs.getDouble("ValorDesconto")
            item.valorRepasse = rs.getDouble("ValorRepasse")
            item.valorUnitario = rs.getDouble("ValorUnitario")
            item.fracionamento = rs.getDouble("Fracionamento")


            return item
        }

        private const val findItensNotaByIdPedidoPalm = "" +
                "DECLARE @IDPEDIDOPALM INT\n" +
                "SET @IDPEDIDOPALM = :id\n" +
                "\n" +
                "SELECT im.ValorItemSubICMS, CodProdutoArq, CodProduto, im.Qtd, im.UnidItemMov,\n" +
                "PrecoUnitLiq = dbo.fn_ValorItemMov4('U', im.IdMov, im.IdItemMov, im.IdProduto, im.PrecoUnit, im.PercDescontoItem, im.Qtd, im.PercICMS, im.PercIPI, im.PercISS, im.MVAitem, TipoMov, Mov.CodCliFor, Mov.CodFilial, CodFilialDest, CodLocalDest, Mov.PercDesconto, CalculaICMSsubstituto,  DescNaoIncideICMS, im.IdNaturezaOperacao, SitTributariaItem,ValorIPIincideICMS,ValorFreteIncideICMS,PercReducaoBICMS,PercSubICMS, ValorItemFrete, ValorItemOutDespesas),\n" +
                "im.PercDescontoItem, ValorDesconto = 0, ValorRepasse = 0, Repasse = 0, ValorUnitario = im.PrecoUnit, Fracionamento = (im.Qtd / im.FatorConvUnid) / im.Qtd\n" +
                "FROM ItemPedidoPalm \n" +
                "INNER JOIN Movimento Mov ON ItemPedidoPalm.IdPedidoPalm = Mov.IdPedidoPalm \n" +
                "Inner JOIN ItensMov im ON Mov.IdMov = im.IdMov AND ItemPedidoPalm.IdProduto = im.IdProduto\n" +
                "WHERE Mov.IdPedidoPalm = @IDPEDIDOPALM"
    }
}