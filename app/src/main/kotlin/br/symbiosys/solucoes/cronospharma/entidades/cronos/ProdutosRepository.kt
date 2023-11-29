package br.symbiosys.solucoes.cronospharma.entidades.cronos

import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.time.LocalDateTime

@Repository
class ProdutosRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {


    val logger = LoggerFactory.getLogger(ProdutosRepository::class.java)
    fun findAll(): List<Produtos> {
        return jdbcTemplate.query(selectProdutos, mapperProdutos)
    }

    fun findById(id: Int): Produtos? {
        return jdbcTemplate.query(selectProdutos + " WHERE idProduto = $id", mapperProdutos).firstOrNull()
    }

    fun findByIdGrupoAndFabricantes(idgrupos: String, codFabricantes: String): Produtos? {
        return jdbcTemplate.query(
            selectProdutos + " WHERE p.IdGrupo IN ($idgrupos) AND p.CodFabr IN (codFabricantes)",
            mapperProdutos
        ).firstOrNull()
    }

    fun updateSalesID(codProduto: String, salesId: String) {
        jdbcTemplate.update(
            "UPDATE Produtos SET MedicReferencia = :salesId WHERE CodProduto = :id",
            MapSqlParameterSource("salesId", salesId).addValue("id", codProduto)
        )
    }

    fun findEstoqueByCodFilialAndCodLocal(codFilial: String, codLocal: String): List<Estoque> {
        return jdbcTemplate.query(
            selectEstoque,
            MapSqlParameterSource().addValue("codfilial", codFilial).addValue("codlocal", codLocal),
            mapperEstoque
        )
    }

    fun findPrecos(): MutableList<TabelaPreco> {
        return jdbcTemplate.query(selectTabelaPreco, mapperTabeloPreco)


    }

    companion object {
        private val mapperProdutos = RowMapper<Produtos> { rs: ResultSet, rowNum: Int ->
            Produtos().apply {
                idProduto = rs.getInt("IdProduto")
                codProduto = rs.getString("CODPRODUTO")
                nomeProduto = rs.getString("NOMEPRODUTO")
                descricaoProduto = rs.getString("DscProduto")
                unidadeMedida = rs.getString("Unid")
                volume = rs.getString("Volume").toDouble()
                codigoGrupo = rs.getString("CodGrupo")
                fabricante = rs.getString("NomeFabr")
                ativo = if (rs.getString("ProdutoInativo") == "S") false else true
                referenciaMedica = rs.getString("MedicReferencia")
            }
        }


        private val mapperTabeloPreco = RowMapper<TabelaPreco> { rs: ResultSet, rowNum: Int ->
            TabelaPreco().apply {
                codigoProduto = rs.getString("CODPRODUTO")
                preco1 = rs.getDouble("PrecoVenda1")
                preco2 = rs.getDouble("PrecoVenda2")
                preco3 = rs.getDouble("PrecoVenda3")
                preco4 = rs.getDouble("PrecoVenda4")
                preco5 = rs.getDouble("PrecoVenda5")
                preco6 = rs.getDouble("PrecoVenda6")
                descontoMaximo = rs.getDouble("PercDescMax")
                codigoSales = rs.getString("MedicReferencia")
                status = rs.getString("StatusProduto")
                fabricante = rs.getString("FABRICANTE")
                referenciaFabricante = rs.getString("CodProdutoFabr")
            }
        }

        val selectProdutos = "" +
                "SELECT p.IdProduto, CODPRODUTO, NOMEPRODUTO, DscProduto, Unid,\n" +
                "Volume = ISNULL(zc.Litros,0), g.CodGrupo, f.NomeFabr, p.ProdutoInativo, p.MedicReferencia\n" +
                "FROM Produtos p INNER JOIN Grupos g ON p.IdGrupo = g.IdGrupo \n" +
                "INNER JOIN Fabricantes f ON p.CodFabr = f.CodFabr\n" +
                "LEFT JOIN ZProdutosCompl zc ON p.IdProduto = zc.IdProduto\n"


        val selectTabelaPreco = "" +
                "SELECT \n" +
                "CODPRODUTO, PrecoVenda1, PrecoVenda2, PrecoVenda3, PrecoVenda4, PrecoVenda5, PrecoVenda6,\n" +
                "PercDescMax, MedicReferencia, StatusProduto, CodProdutoFabr, FABRICANTE = (SELECT NomeFabr FROM Fabricantes WHERE CodFabr = p.CodFabr)\n" +
                "\n" +
                "FROM Produtos p \n" +
                ""


        val selectEstoque = "" +
                "SELECT Produtos.CODPRODUTO, Estoque.SdoAtual, FABRICANTE = (SELECT NomeFabr FROM Fabricantes WHERE CodFabr = Produtos.CodFabr), Produtos.CodProdutoFabr FROM Estoque " +
                "LEFT JOIN Produtos ON Estoque.IdProduto = Produtos.IdProduto\n" +
                " WHERE Codfilial = :codfilial AND CodLocal = :codlocal AND Produtos.MedicReferencia IS NOT NULL " +
                "UNION\n" +
                "SELECT Produtos.CODPRODUTO, Estoque.SdoAtual, FABRICANTE = (SELECT NomeFabr FROM Fabricantes WHERE CodFabr = Produtos.CodFabr), Produtos.CodProdutoFabr FROM Estoque " +
                "LEFT JOIN Produtos ON Estoque.IdProduto = Produtos.IdProduto\n" +
                " WHERE Codfilial = :codfilial AND CodLocal = :codlocal AND (SELECT NomeFabr FROM Fabricantes WHERE CodFabr = Produtos.CodFabr) = 'PETRONAS'"

        private val mapperEstoque = RowMapper<Estoque> { rs: ResultSet, rowNum: Int ->
            Estoque().apply {
                codigoProduto = rs.getString("CODPRODUTO")
                sdoAtual = rs.getDouble("SdoAtual")
                referenciaFabricante = rs.getString("CodProdutoFabr")
                fabricante = rs.getString("FABRICANTE")
            }
        }
    }

}

class Estoque {
    var codigoProduto: String? = null
    var sdoAtual = 0.0
    var referenciaFabricante: String? = ""
    var fabricante: String? = ""
}

class TabelaPreco {
    var activePrice = ""
    var referenciaFabricante: String? = null
    var fabricante: String? = null
    var codigoProduto: String? = null
    var nomeTabela: String? = null
    var preco1: Double = 0.0
    var preco2: Double = 0.0
    var preco3: Double = 0.0
    var preco4: Double = 0.0
    var preco5: Double = 0.0
    var preco6: Double = 0.0
    var status: String? = null
    var descontoMaximo: Double = 0.0
    var promocaoAtiva = false
    var dataInicioPromocao: LocalDateTime? = null
    var dataFimPromocao: LocalDateTime = LocalDateTime.now().minusDays(1)
    var codigoSales: String? = null

}



