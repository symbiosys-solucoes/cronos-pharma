package br.symbiosys.solucoes.cronospharma.modules.petronas.ports.repositories.implementations

import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.KeyProductsInventory
import br.symbiosys.solucoes.cronospharma.modules.petronas.ports.repositories.ProductsInventoryPetronasRepository
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class ProductsInventoryPetronasRepositoryImpl(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) : ProductsInventoryPetronasRepository {

    override fun findAll(): MutableList<KeyProductsInventory> {
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