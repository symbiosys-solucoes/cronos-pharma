package br.symbiosys.solucoes.cronospharma.modules.petronas.ports.repositories

import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.KeyProductsInventory
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class ProductsInventoryPetronasRepository {

    @Autowired
    lateinit var jdbcTemplate: NamedParameterJdbcTemplate

    private val logger = LoggerFactory.getLogger(ProductsInventoryPetronasRepository::class.java)
    fun findAll(): MutableList<KeyProductsInventory> {
        return jdbcTemplate.query("SELECT * FROM sym_petronas_inventory", inventoryProductsMapper)
    }

    companion object {
        val inventoryProductsMapper = RowMapper<KeyProductsInventory> { rs: ResultSet, rowNum: Int ->
            KeyProductsInventory().apply {
                productCode = rs.getString("ProductCode")
                dtCode = rs.getString("DTCode")
                inventoryQuantity = rs.getDouble("Inventory")
                manufacture = rs.getString("Manufacturer")
                active = true
            }
        }
    }


}
