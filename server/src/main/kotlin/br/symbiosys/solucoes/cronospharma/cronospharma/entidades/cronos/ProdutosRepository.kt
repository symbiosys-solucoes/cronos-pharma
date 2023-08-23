package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos

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

    fun findPrecos(): MutableList<TabelaPreco> {
        val parametrosPreco = jdbcTemplate.query(selectParametrosPreco, mapperParametrosPreco).first()
        val tabelaPreco = jdbcTemplate.query(selectTabelaPreco, mapperTabeloPreco)
        val tabelas = mutableListOf<TabelaPreco>()
        if (!parametrosPreco.nomeTabela1.isNullOrBlank()) {
            for (tabela in tabelaPreco) {
                tabela.nomeTabela = parametrosPreco.nomeTabela1
                tabela.activePrice = "1"
                if (parametrosPreco.idPrecoPromocao == 1 && parametrosPreco.dataFimPromocao!!.isAfter(LocalDateTime.now())
                ) {
                    tabela.promocaoAtiva = true
                    tabela.dataInicioPromocao = parametrosPreco.dataInicioPromocao
                    tabela.dataFimPromocao = parametrosPreco.dataFimPromocao!!
                }
                tabelas.add(tabela)
            }

        }
        if (!parametrosPreco.nomeTabela2.isNullOrBlank()) {
            for (tabela in tabelaPreco) {
                tabela.nomeTabela = parametrosPreco.nomeTabela2
                tabela.activePrice = "2"
                if (parametrosPreco.idPrecoPromocao == 2 && parametrosPreco.dataFimPromocao!!
                        .isAfter(LocalDateTime.now())
                ) {
                    tabela.promocaoAtiva = true
                    tabela.dataInicioPromocao = parametrosPreco.dataInicioPromocao
                    tabela.dataFimPromocao = parametrosPreco.dataFimPromocao!!
                }
                tabelas.add(tabela)
            }
        }
        if (!parametrosPreco.nomeTabela3.isNullOrBlank()) {
            for (tabela in tabelaPreco) {
                tabela.nomeTabela = parametrosPreco.nomeTabela3
                tabela.activePrice = "3"
                if (parametrosPreco.idPrecoPromocao == 3 && parametrosPreco.dataFimPromocao!!
                        .isAfter(LocalDateTime.now())
                ) {
                    tabela.promocaoAtiva = true
                    tabela.dataInicioPromocao = parametrosPreco.dataInicioPromocao
                    tabela.dataFimPromocao = parametrosPreco.dataFimPromocao!!
                }
                tabelas.add(tabela)
            }

        }
        if (!parametrosPreco.nomeTabela4.isNullOrBlank()) {
            for (tabela in tabelaPreco) {
                tabela.nomeTabela = parametrosPreco.nomeTabela4
                tabela.activePrice = "4"
                if (parametrosPreco.idPrecoPromocao == 4 && parametrosPreco.dataFimPromocao!!
                        .isAfter(LocalDateTime.now())
                ) {
                    tabela.promocaoAtiva = true
                    tabela.dataInicioPromocao = parametrosPreco.dataInicioPromocao
                    tabela.dataFimPromocao = parametrosPreco.dataFimPromocao!!
                }
                tabelas.add(tabela)
            }

        }
        if (!parametrosPreco.nomeTabela5.isNullOrBlank()) {
            for (tabela in tabelaPreco) {
                tabela.nomeTabela = parametrosPreco.nomeTabela5
                tabela.activePrice = "5"
                if (parametrosPreco.idPrecoPromocao == 5 && parametrosPreco.dataFimPromocao!!
                        .isAfter(LocalDateTime.now())
                ) {
                    tabela.promocaoAtiva = true
                    tabela.dataInicioPromocao = parametrosPreco.dataInicioPromocao
                    tabela.dataFimPromocao = parametrosPreco.dataFimPromocao!!
                }
                tabelas.add(tabela)
            }
        }
        if (!parametrosPreco.nomeTabela6.isNullOrBlank()) {
            for (tabela in tabelaPreco) {
                tabela.nomeTabela = parametrosPreco.nomeTabela6
                tabela.activePrice = "6"
                if (parametrosPreco.idPrecoPromocao == 6 && parametrosPreco.dataFimPromocao!!
                        .isAfter(LocalDateTime.now())
                ) {
                    tabela.promocaoAtiva = true
                    tabela.dataInicioPromocao = parametrosPreco.dataInicioPromocao
                    tabela.dataFimPromocao = parametrosPreco.dataFimPromocao!!
                }
                tabelas.add(tabela)
            }
        }

        return tabelas

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

        private val mapperParametrosPreco = RowMapper<ParametrosPreco> { rs: ResultSet, rowNum: Int ->
            ParametrosPreco().apply {
                nomeTabela1 = rs.getString("NomePrcVenda1")
                nomeTabela2 = rs.getString("NomePrcVenda2")
                nomeTabela3 = rs.getString("NomePrcVenda3")
                nomeTabela4 = rs.getString("NomePrcVenda4")
                nomeTabela5 = rs.getString("NomePrcVenda5")
                nomeTabela6 = rs.getString("NomePrcVenda6")
                promocaoAtiva = if (rs.getString("PromocaoAtiva") == "S") true else false
                dataInicioPromocao = rs.getTimestamp("DataInicioPromocao")?.toLocalDateTime() ?: LocalDateTime.now().minusDays(1)
                dataFimPromocao = rs.getTimestamp("DataFimPromocao")?.toLocalDateTime() ?: LocalDateTime.now().minusDays(1)
                idPrecoPromocao = rs.getInt("IdPrecoPromocao")
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
            }
        }

        val selectProdutos = "" +
                "SELECT p.IdProduto, CODPRODUTO, NOMEPRODUTO, DscProduto, Unid,\n" +
                "Volume = ISNULL(zc.Litros,0), g.CodGrupo, f.NomeFabr, p.ProdutoInativo, p.MedicReferencia\n" +
                "FROM Produtos p INNER JOIN Grupos g ON p.IdGrupo = g.IdGrupo \n" +
                "INNER JOIN Fabricantes f ON p.CodFabr = f.CodFabr\n" +
                "LEFT JOIN ZProdutosCompl zc ON p.IdProduto = zc.IdProduto\n"

        val selectParametrosPreco = "" +
                "SELECT NomePrcVenda1, NomePrcVenda2, NomePrcVenda3, NomePrcVenda4,\n" +
                "NomePrcVenda5, NomePrcVenda6, PromocaoAtiva, DataInicioPromocao, DataFimPromocao, IdPrecoPromocao\n" +
                "FROM Parametros p "
        val selectTabelaPreco = "" +
                "SELECT \n" +
                "CODPRODUTO, PrecoVenda1, PrecoVenda2, PrecoVenda3, PrecoVenda4, PrecoVenda5, PrecoVenda6,\n" +
                "PercDescMax, MedicReferencia, StatusProduto\n" +
                "\n" +
                "FROM Produtos p \n" +
                "WHERE MedicReferencia IS NOT NULL"
    }

}

class TabelaPreco {
    var activePrice = ""
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

class ParametrosPreco {
    var nomeTabela1: String? = ""
    var nomeTabela2: String? = ""
    var nomeTabela3: String? = ""
    var nomeTabela4: String? = ""
    var nomeTabela5: String? = ""
    var nomeTabela6: String? = ""
    var promocaoAtiva = false
    var dataInicioPromocao: LocalDateTime? = null
    var dataFimPromocao: LocalDateTime? = null
    var idPrecoPromocao: Int = 0

}


