package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.repositories

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.request.Products
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class ProductsPetronasRepository {

    @Autowired
    lateinit var jdbcTemplate: NamedParameterJdbcTemplate


    fun findAll(): MutableList<Products> {
        return jdbcTemplate.query("SELECT * FROM sym_petronas_products WHERE ISNULL(PrecisaEnviar,1) = 1", productsMapper)
    }

    fun markAsCreated(codProduto: String){
        val query = "" +
                "DECLARE @IDPRODUTO INT\n" +
                "SET @IDPRODUTO = (SELECT IdProduto FROM Produtos WHERE CODPRODUTO = :codproduto)\n" +
                "IF NOT EXISTS (SELECT 1 FROM ZProdutosCompl WHERE IdProduto = @IDPRODUTO)\n" +
                "BEGIN\n" +
                "INSERT INTO ZProdutosCompl (IdProduto, sym_enviar_petronas) VALUES (@IDPRODUTO, 0)\n" +
                "END\n" +
                "ELSE\n" +
                "BEGIN\n" +
                "UPDATE ZProdutosCompl SET sym_enviar_petronas = 0 WHERE IdProduto = @IDPRODUTO\n" +
                "END\n"
        jdbcTemplate.update(
            query, mapOf(
                "codproduto" to codProduto
            )
        )
    }

    fun markAsUpdated(codProduto: String){
        val query = "" +
                "DECLARE @IDPRODUTO INT\n" +
                "SET @IDPRODUTO = (SELECT IdProduto FROM Produtos WHERE CODPRODUTO = :codproduto)\n" +
                "IF NOT EXISTS (SELECT 1 FROM ZProdutosCompl WHERE IdProduto = @IDPRODUTO)\n" +
                "BEGIN\n" +
                "INSERT INTO ZProdutosCompl (IdProduto, sym_enviar_petronas) VALUES (@IDPRODUTO, 0)\n" +
                "END\n" +
                "ELSE\n" +
                "BEGIN\n" +
                "UPDATE ZProdutosCompl SET sym_enviar_petronas = 0 WHERE IdProduto = @IDPRODUTO\n" +
                "END\n"
        jdbcTemplate.update(
            query, mapOf(
                "codproduto" to codProduto
            )
        )
    }

    companion object {
        val productsMapper = RowMapper<Products> { rs: ResultSet, rowNum: Int ->
            Products().apply {
                productCode = rs.getString("ProductCode")
                dtCode = rs.getString("DTCode")
                name = rs.getString("ProductName")
                description = rs.getString("ProductDescription")
                unid = rs.getString("PackType")
                unidQuantity = rs.getString("PackVol")
                category = rs.getString("Category")
                manufacture = rs.getString("Manufacturer")
                active = true
            }
        }
    }



}
