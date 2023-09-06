package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.repositories

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.request.Accounts
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet


@Repository
class PetronasAccountsRepository {

    @Autowired
    private lateinit var jdbcTemplate: NamedParameterJdbcTemplate

    fun findAll(): List<Accounts> {
        return jdbcTemplate.query("SELECT * FROM dbo.sym_petronas_account WHERE PrecisaEnviar = 1", accountsMapper)
    }

    fun markAsCreated(salesId: String, accountNumber: String) {
        val query = "" +
                "IF NOT EXISTS (SELECT 1 FROM ZCli_ForCompl WHERE CodClifor = :account)\n" +
                "BEGIN\n" +
                "INSERT INTO ZCli_ForCompl (CodClifor, sym_enviar_petronas) VALUES (:account, 0)\n" +
                "END\n" +
                "ELSE\n" +
                "BEGIN\n" +
                "UPDATE ZCli_ForCompl SET sym_enviar_petronas = 0 WHERE CodClifor = :account\n" +
                "END\n" +
                "UPDATE Cli_FOR SET CampoAlfaOp1 = :salesid WHERE CodClifor = :account"
        jdbcTemplate.update(
            query, mapOf(
                "account" to accountNumber,
                "salesid" to salesId
            )
        )
    }

    fun markAsUpdated(salesId: String, accountNumber: String) {
        val query = "" +
                "IF NOT EXISTS (SELECT 1 FROM ZCli_ForCompl WHERE CodClifor = :account)\n" +
                "BEGIN\n" +
                "INSERT INTO ZCli_ForCompl (CodClifor, sym_enviar_petronas) VALUES (:account, 0)\n" +
                "END\n" +
                "ELSE\n" +
                "BEGIN\n" +
                "UPDATE ZCli_ForCompl SET sym_enviar_petronas = 0 WHERE CodClifor = :account\n" +
                "END\n"

        jdbcTemplate.update(
            query, mapOf(
                "account" to accountNumber,
                "salesid" to salesId
            )
        )
    }


    companion object {
        val accountsMapper = RowMapper<Accounts> { rs: ResultSet, rowNum: Int ->
            Accounts().apply {
                salesForceId = rs.getString("ID")
                cnpj = rs.getString("CNPJ")
                accountNumber = rs.getString("AccountNumber")
                dtCode = rs.getString("DTCode")
                accountName = rs.getString("AccountName")
                fantasyName = rs.getString("FantasyName")
                customerType = rs.getString("CustomerType")
                bussinessType = rs.getString("BussinesType")
                accountSource = rs.getString("AccountSource")
                zipCode = rs.getString("ZipCode")
                phone = rs.getString("Phone")
                phone1 = rs.getString("Phone1")
                webSite = rs.getString("WebSite")
                address1 = rs.getString("Address1")
                address2 = rs.getString("Address2")
                city = rs.getString("City")
                neighborhood = rs.getString("Neighborhood")
                state = rs.getString("State")
                userCode = rs.getString("UserCode")
                creditLimit = rs.getDouble("CreditLimit")
                paymentMethod = rs.getString("PaymentMethod")
                paymentCondition = rs.getString("PaymentTerms")
                active = true
                activePriceBook = rs.getString("ActivePriceBook")
            }
        }
    }

}
