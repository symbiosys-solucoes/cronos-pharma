package br.symbiosys.solucoes.cronospharma.modules.petronas.ports.repositories.implementations

import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.KeyProducts
import br.symbiosys.solucoes.cronospharma.modules.petronas.ports.repositories.ProductsKeyPetronasRepository
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class ProductsKeyPetronasRepositoryImpl(private val jdbcTemplate: NamedParameterJdbcTemplate) :
    ProductsKeyPetronasRepository {

    val logger = LoggerFactory.getLogger(ProductsKeyPetronasRepositoryImpl::class.java)


    override fun findAll(): MutableList<KeyProducts> {
        return jdbcTemplate.query("SELECT * FROM sym_petronas_key_products", keyProductsMapper)
    }

    override fun markAsCreated(codProduto: String) {
        val query = "" +
                "DECLARE @IDPRODUTO INT\n" +
                "SET @IDPRODUTO = ISNULL((SELECT IdProduto FROM Produtos WHERE CODPRODUTO = :codproduto), (SELECT IdProduto FROM Produtos WHERE CodProdutoFabr = :codproduto))\n" +
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
            logger.error("erro ao marcar atualizacao do produto $codProduto", e)
        }

    }

    override fun markAsUpdated(codProduto: String) {
        val query = "" +
                "DECLARE @IDPRODUTO INT\n" +
                "SET @IDPRODUTO = ISNULL((SELECT IdProduto FROM Produtos WHERE CODPRODUTO = :codproduto), (SELECT IdProduto FROM Produtos WHERE CodProdutoFabr = :codproduto))\n" +
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
            logger.error("erro ao marcar atualizacao do produto $codProduto", e)
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