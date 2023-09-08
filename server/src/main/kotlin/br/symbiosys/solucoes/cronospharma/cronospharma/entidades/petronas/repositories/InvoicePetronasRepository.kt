package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.repositories

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos.BloqueioMovimentoRepository
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.request.Invoice
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.request.InvoiceLine
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.request.Order
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.request.OrderItem
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class InvoicePetronasRepository {

    val logger = LoggerFactory.getLogger(InvoicePetronasRepository::class.java)

    @Autowired
    private lateinit var jdbcTemplate: NamedParameterJdbcTemplate

    fun markAsUpdated(salesid: String){
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

    fun markAsCreated(codFilial: String, numero: String, salesid: String) {
        val query = "" +
                "DECLARE @IDMOV INT\n" +
                "SET @IDMOV = (SELECT IdMov FROM Movimento WHERE CodFilial = :filial AND NumMov = :numero AND TipoMov = '2.4')\n" +
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
    fun findAll(enviados: Boolean = false): List<Invoice> {
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