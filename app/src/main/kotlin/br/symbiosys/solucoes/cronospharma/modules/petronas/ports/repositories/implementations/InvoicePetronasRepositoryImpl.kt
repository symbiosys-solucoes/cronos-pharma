package br.symbiosys.solucoes.cronospharma.modules.petronas.ports.repositories.implementations

import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.Invoice
import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.InvoiceLine
import br.symbiosys.solucoes.cronospharma.modules.petronas.ports.repositories.InvoicePetronasRepository
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.time.LocalDate

@Repository
class InvoicePetronasRepositoryImpl(private val jdbcTemplate: NamedParameterJdbcTemplate) : InvoicePetronasRepository {

    val logger = LoggerFactory.getLogger(InvoicePetronasRepositoryImpl::class.java)


    override fun markAsUpdated(salesid: String) {
        val query = "" +
                "DECLARE @IDMOV INT\n" +
                "SET @IDMOV = (SELECT IdMov FROM Movimento WHERE CampoAlfaMovOp1 = :salesid AND TipoMov = '2.4')\n" +
                "IF @IDMOV IS NOT NULL\n" +
                "BEGIN\n" +
                "\tIF NOT EXISTS (SELECT 1 FROM ZMovimentoCompl WHERE IdMov = @IDMOV)\n" +
                "\tBEGIN\n" +
                "\t\tINSERT INTO ZMovimentoCompl (IdMov, sym_enviar_petronas) VALUES (@IDMOV, 0)\n" +
                "\tEND\n" +
                "\tELSE\n" +
                "\tBEGIN\n" +
                "\t\tUPDATE ZMovimentoCompl SET sym_enviar_petronas = 0 WHERE IdMov = @IDMOV\n" +
                "\tEND\n" +
                "\n" +
                "END"
        jdbcTemplate.update(
            query,
            mapOf(
                "salesid" to salesid,
            )
        )
    }

    override fun markAsCreated(codFilial: String, numero: String, salesid: String) {
        val query = "" +
                "DECLARE @IDMOV INT\n" +
                "SET @IDMOV = (SELECT IdMov FROM Movimento WHERE CodFilial = :filial AND NumMov = :numero AND TipoMov = '2.4')\n" +
                "IF @IDMOV IS NOT NULL\n" +
                "BEGIN\n" +
                "\tIF NOT EXISTS (SELECT 1 FROM ZMovimentoCompl WHERE IdMov = @IDMOV) AND ISNULL(@IDPRODUTO,0) > 0\n" +
                "\tBEGIN\n" +
                "\t\tINSERT INTO ZMovimentoCompl (IdMov, sym_enviar_petronas) VALUES (@IDMOV, 0)\n" +
                "\tEND\n" +
                "\tELSE\n" +
                "\tBEGIN\n" +
                "\t\tUPDATE ZMovimentoCompl SET sym_enviar_petronas = 0 WHERE IdMov = @IDMOV\n" +
                "\tEND\n" +
                "\n" +
                "\tUPDATE Movimento SET CampoAlfaMovOp1 = :salesid WHERE IdMov = @IDMOV\n" +
                "END"
        jdbcTemplate.update(
            query,
            mapOf(
                "filial" to codFilial,
                "salesid" to salesid,
                "numero" to numero
            )
        )
    }

    override fun findAll(enviados: Boolean): List<Invoice> {
        return jdbcTemplate.query("SELECT * FROM sym_petronas_invoices WHERE PrecisaEnviar = 1", mapperInvoicePetronas)
    }

    override fun findAll(initialDate: LocalDate?, endDate: LocalDate?, erpInvoiceNumber: String?): List<Invoice> {
        if(erpInvoiceNumber != null) {
            return jdbcTemplate.query("SELECT * FROM sym_petronas_invoices WHERE InvoiceDocumentNumber = :numpedido", MapSqlParameterSource("numpedido", erpInvoiceNumber), mapperInvoicePetronas)
        }
        if (initialDate != null && endDate != null) {
            return jdbcTemplate.query("SELECT * FROM sym_petronas_invoices WHERE InvoiceDate BETWEEN :initialDate AND :endDate",
                MapSqlParameterSource("initialDate", initialDate).addValue("endDate", endDate), mapperInvoicePetronas
            )
        }
        return jdbcTemplate.query("SELECT * FROM sym_petronas_invoices WHERE PrecisaEnviar = 1", mapperInvoicePetronas)
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

        private val mapperInvoicePetronas = RowMapper<Invoice> { rs: ResultSet, rowNum: Int ->
            Invoice().apply {
                ebMobileOrderNumber = rs.getString("OrderNumber")
                dtCode = rs.getString("DTCode")
                invoiceDocumentNumber = rs.getString("InvoiceDocumentNumber")
                invoiceSerialNumber = rs.getString("InvoiceSerialNumber")
                ebMobileBillingDate = rs.getString("InvoiceDate")
                ebMobilePaymentTerms = rs.getString("PaymentTerms")
                ebMobileAccountNumber = rs.getString("AccountNumber")
                ebMobileDeliveryNumber = rs.getString("DeliveryNumber")
                ebMobileInvoiceType = rs.getString("InvoiceType")
                ebMobileSalesOrg = rs.getString("SalesOrg")
                ebMobileStatus = rs.getString("Status")
                ebMobileNetValue = rs.getDouble("InvoiceAmount")

            }
        }
    }
}