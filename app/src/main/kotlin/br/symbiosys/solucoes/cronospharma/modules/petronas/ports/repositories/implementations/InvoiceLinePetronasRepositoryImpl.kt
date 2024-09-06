package br.symbiosys.solucoes.cronospharma.modules.petronas.ports.repositories.implementations

import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.InvoiceLine
import br.symbiosys.solucoes.cronospharma.modules.petronas.ports.repositories.InvoiceLinePetronasRepository
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class InvoiceLinePetronasRepositoryImpl(private val jdbcTemplate: NamedParameterJdbcTemplate) :
    InvoiceLinePetronasRepository {

    val logger = LoggerFactory.getLogger(InvoiceLinePetronasRepositoryImpl::class.java)


    override fun markAsUpdated(idItem: Int, salesid: String) {
        val query = "" +
                "UPDATE ItensMov SET CampoAlfaImOp1 = :salesid WHERE IdItemMov = :iditem"
        jdbcTemplate.update(
            query,
            mapOf(
                "salesid" to salesid,
                "iditem" to idItem
            )
        )
    }

    override fun markAsCreated(idItem: Int, salesid: String) {
        val query = "" +
                "UPDATE ItensMov SET CampoAlfaImOp1 = :salesid WHERE IdItemMov = :iditem"
        jdbcTemplate.update(
            query,
            mapOf(
                "salesid" to salesid,
                "iditem" to idItem
            )
        )
    }

    override fun findAll(salesid: String): List<InvoiceLine> {
        return jdbcTemplate.query(
            "SELECT * FROM sym_petronas_invoice_lines WHERE InvoiceSalesId = '${salesid}'",
            mapperInvoiceLinePetronas
        )
    }


    companion object {

        private val mapperInvoiceLinePetronas = RowMapper<InvoiceLine> { rs: ResultSet, rowNum: Int ->
            InvoiceLine().apply {
                invoiceDocumentNumber = rs.getString("InvoiceDocumentNumber")
                invoiceSerialNumber = rs.getString("InvoiceSerialNumber")
                ebMobileInvoiceItemNumber = rs.getString("InvoiceItemNumber")
                dtCode = rs.getString("DTCode")
                manufacture = rs.getString("ManuFacture")
                ebMobileInvoiceQuantity = rs.getDouble("InvoiceQuantity")
                unitPrice = rs.getDouble("UnitPrice")
                lineTotalFinal = rs.getDouble("LineTotalFinal")
                ebMobileLineDiscountAmount = rs.getDouble("LineDiscountAmount")
                ebMobileProductCode = rs.getString("ProductCode")
                ebMobileReferenceDocument = rs.getString("RefenceDocument")
                ebMobileUsageIndicator = rs.getString("UsageIndicator")
                ebMobileIsActive = true
            }
        }

    }
}