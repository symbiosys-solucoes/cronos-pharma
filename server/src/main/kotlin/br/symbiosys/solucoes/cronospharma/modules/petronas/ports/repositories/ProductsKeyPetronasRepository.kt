package br.symbiosys.solucoes.cronospharma.modules.petronas.ports.repositories

import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.KeyProducts
import org.slf4j.LoggerFactory

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class ProductsKeyPetronasRepository {

    @Autowired
    lateinit var jdbcTemplate: NamedParameterJdbcTemplate

    val logger = LoggerFactory.getLogger(ProductsKeyPetronasRepository::class.java)


    fun findAll(): MutableList<KeyProducts> {
        return jdbcTemplate.query("SELECT * FROM sym_petronas_key_products", keyProductsMapper)
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
        try {
            jdbcTemplate.update(
                query, mapOf(
                    "codproduto" to codProduto
                )
            )
        } catch (e: Exception) {
            logger.error("erro ao marcar atualizacao do produto $codProduto")
        }

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
        try {
            jdbcTemplate.update(
                query, mapOf(
                    "codproduto" to codProduto
                )
            )
        } catch (e: Exception) {
            logger.error("erro ao marcar atualizacao do produto $codProduto")
        }
    }

    companion object {
        val keyProductsMapper = RowMapper<KeyProducts> { rs: ResultSet, rowNum: Int ->
            KeyProducts().apply {
                productCode = rs.getString("ProductCode")
                dtCode = rs.getString("DTCode")
                basePrice = rs.getDouble("BaseUnitPrice")
                maxDiscount = rs.getDouble("MaxDiscountPercentage")
                promotionPrice = rs.getDouble("PromotionUnitPrice")
                promotionStartDate = rs.getTimestamp("StartDate")?.toLocalDateTime()?.toLocalDate()
                promotionEndDate = rs.getTimestamp("EndDate")?.toLocalDateTime()?.toLocalDate()
                priceBookName = rs.getString("PriceBok")
                manufacture = rs.getString("Manufacturer")
                active = true
            }
        }
    }



}