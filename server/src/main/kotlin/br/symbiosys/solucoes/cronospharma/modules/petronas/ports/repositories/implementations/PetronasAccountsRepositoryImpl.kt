package br.symbiosys.solucoes.cronospharma.modules.petronas.ports.repositories.implementations

import br.symbiosys.solucoes.cronospharma.modules.petronas.models.request.Accounts
import br.symbiosys.solucoes.cronospharma.modules.petronas.ports.repositories.PetronasAccountsRepository
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class PetronasAccountsRepositoryImpl(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) : PetronasAccountsRepository {

    override fun findAll(): List<Accounts> {
        return jdbcTemplate.query("SELECT * FROM dbo.sym_petronas_account WHERE PrecisaEnviar = 1", accountsMapper)
    }

    override fun markAsCreated(salesId: String, accountNumber: String) {
        val query = "UPDATE Cli_FOR SET CampoAlfaOp1 = :salesid, ValorOp1 = 0 WHERE CodClifor = :account"
        jdbcTemplate.update(
            query, mapOf(
                "account" to accountNumber,
                "salesid" to salesId
            )
        )
    }

    override fun markAsUpdated(salesId: String, accountNumber: String) {
        val query = "UPDATE Cli_FOR SET  ValorOp1 = 0 WHERE CodClifor = :account"

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
                active = if (rs.getString("Active") == "S") true else false
                activePriceBook = rs.getString("ActivePriceBook")
            }
        }
    }

}