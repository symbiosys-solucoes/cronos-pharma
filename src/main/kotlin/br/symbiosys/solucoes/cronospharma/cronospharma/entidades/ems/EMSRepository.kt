package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.ems

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.diretorios.Diretorio
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.io.File
import java.sql.ResultSet

@Repository
class EMSRepository (
    private val jdbcTemplate: NamedParameterJdbcTemplate
){
    @Value("\${app.filial}")
    lateinit var codFilial: String
    @Value("\${app.filial.local.estoque}")
    lateinit var codLocal: String

    fun findAll(): List<EstoqueEMS>{
        return jdbcTemplate.query(findEstoque, MapSqlParameterSource("codFilial", codFilial).addValue("codLocal", codLocal),
            findEstoqueMapper)
    }

    companion object{
        private val findEstoqueMapper = RowMapper<EstoqueEMS> { rs: ResultSet, rowNum: Int ->
            EstoqueEMS(
                codigoBarras = rs.getString("CodigoBarras"),
                quantidade = rs.getDouble("quantidade")
            )
        }
        private val findEstoque = "" +
                "SELECT CodigoBarras, NomeProduto, quantidade=ISNULL(sdoAtual, 0)\n" +
                "FROM CodigoBarras cb, Produtos Prod LEFT JOIN Estoque Est ON prod.IdProduto = Est.IdProduto\n" +
                "WHERE cb.idproduto = prod.idproduto\n" +
                "\n" +
                "AND Est.CodFilial = :codFilial\n" +
                "AND Est.CodLocal = :codLocal"
    }
}

data class EstoqueEMS( val codigoBarras: String, val quantidade: Double)

fun geraEstoqueEMS(cnpj: String, repository: EMSRepository, diretorio: Diretorio): File {
    val estoque = repository.findAll()
    var conteudo = StringBuilder()

    estoque.forEach {
        conteudo.append("80${it.codigoBarras}${StringUtils.leftPad(it.quantidade.toInt().toString(),6, "0")}\n")
    }
    val nomeDoArquivo = "ESTEMS_$cnpj.txt"

    val file = File(diretorio.diretorioEstoqueLocal + nomeDoArquivo)
    file.writeText(conteudo.toString())
    return file
}