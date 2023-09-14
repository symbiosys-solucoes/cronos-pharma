package br.symbiosys.solucoes.cronospharma.modules.petronas.ports.repositories.implementations

import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.AccountAR
import br.symbiosys.solucoes.cronospharma.modules.petronas.ports.repositories.PetronasAccountsARRepository
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class PetronasAccountsARRepositoryImpl(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) : PetronasAccountsARRepository {


    override fun findAll(): List<AccountAR> {
        return jdbcTemplate.query("SELECT * FROM sym_petronas_account_ars WHERE PrecisaEnviar = 1", accountsARMapper)
    }

    override fun markAsCreated(salesId: String, idCpr: Int) {
        val query = "" +
                "IF NOT EXISTS (SELECT 1 FROM ZCPRCompl WHERE IdCPR = :id)\n" +
                "BEGIN\n" +
                "INSERT INTO ZCPRCompl (IdCPR, sym_enviar_petronas) VALUES (:id, 0)\n" +
                "END\n" +
                "ELSE\n" +
                "BEGIN\n" +
                "UPDATE ZCPRCompl SET sym_enviar_petronas = 0 WHERE IdCPR = :id\n" +
                "END\n" +
                "UPDATE CPR SET NumDocAux = :salesid WHERE IdCPR = :id"
        jdbcTemplate.update(
            query, mapOf(
                "id" to idCpr,
                "salesid" to salesId
            )
        )
    }

    override fun markAsUpdated(salesId: String, idCpr: Int) {
        val query = "" +
                "IF NOT EXISTS (SELECT 1 FROM ZCPRCompl WHERE IdCPR = :id)\n" +
                "BEGIN\n" +
                "INSERT INTO ZCPRCompl (IdCPR, sym_enviar_petronas) VALUES (:id, 0)\n" +
                "END\n" +
                "ELSE\n" +
                "BEGIN\n" +
                "UPDATE ZCPRCompl SET sym_enviar_petronas = 0 WHERE IdCPR = :id\n" +
                "END\n"

        jdbcTemplate.update(
            query, mapOf(
                "account" to idCpr,
                "salesid" to salesId
            )
        )
    }


    companion object {
        val accountsARMapper = RowMapper<AccountAR> { rs: ResultSet, rowNum: Int ->
            AccountAR().apply {
                salesForceId = rs.getString("SalesId")
                creditLimit = rs.getDouble("CreditLimit")
                invoiceAmount = rs.getDouble("InvoiceAmount")
                invoiceDate = rs.getTimestamp("InvoiceDate")?.toLocalDateTime()
                invoiceDueDate = rs.getTimestamp("InvoiceDueDate")?.toLocalDateTime()
                invoiceNumber = rs.getString("InvoiceNumber")
                accountNumber = rs.getString("AccountNumber")
                invoiceOpenAmount = rs.getDouble("OpenAmount")
                paymentDate = rs.getTimestamp("PaymentDate")?.toLocalDateTime()
                dtCode = rs.getString("DTCode")
                remarks = rs.getString("Remarks")
            }
        }
    }

}