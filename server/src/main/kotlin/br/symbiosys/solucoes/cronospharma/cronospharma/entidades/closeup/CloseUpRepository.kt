package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.closeup

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.diretorios.Diretorio
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.io.File
import java.sql.ResultSet
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Repository
class CloseUpRepository (
    private val jdbcTemplate: NamedParameterJdbcTemplate
){
    @Value("\${app.filial}")
    lateinit var codFilial: String
    @Value("\${app.filial.local.estoque}")
    lateinit var codLocal: String

    fun findAll(): List<ItemEstoque>{
        return jdbcTemplate.query(findEstoque, MapSqlParameterSource("codFilial", codFilial).addValue("codLocal", codLocal),
            findEstoqueMapper)
    }

    companion object{
        private val findEstoqueMapper = RowMapper<ItemEstoque> { rs: ResultSet, rowNum: Int ->
             ItemEstoque().apply {
                codigoBarras = rs.getString("CodigoBarras")
                quantidade = rs.getDouble("quantidade").toInt()
                 preco = rs.getDouble("PrecoVenda1")
            }


        }
        private val findEstoque = "" +
                "SELECT CodigoBarras, NomeProduto, PrecoVenda1, quantidade=ISNULL(sdoAtual, 0)\n" +
                "FROM CodigoBarras cb, Produtos Prod LEFT JOIN Estoque Est ON prod.IdProduto = Est.IdProduto\n" +
                "WHERE cb.idproduto = prod.idproduto\n" +
                "\n" +
                "AND Est.CodFilial = :codFilial\n" +
                "AND Est.CodLocal = :codLocal"
    }
}

fun geraPrecoCloseUp(cnpj: String, repository: CloseUpRepository, diretorio: Diretorio): File {
    val format = DateTimeFormatter.ofPattern("ddMMyyyy")
    val estoqueCloseup = Estoque()
    estoqueCloseup.cnpjDistribuidor = cnpj
    estoqueCloseup.dataPosicaoEstoque = LocalDateTime.now().format(format)


    val estoque = repository.findAll()
    var conteudo = StringBuilder()

    conteudo.append(estoqueCloseup.tipoRegistro)
    conteudo.append(cnpj)
    conteudo.append(estoqueCloseup.dataPosicaoEstoque)
    conteudo.append(LocalTime.now().format(DateTimeFormatter.ofPattern("HHmm")))
    conteudo.append("\n")


    estoque.forEach {
        conteudo.append(it.tipoRegistro)
        conteudo.append(it.codigoBarras)
        conteudo.append(StringUtils.leftPad(it.preco.toString().replace(".",""),14, "0"))
        conteudo.append("RN")
        conteudo.append("\n")
    }
    conteudo.append("3")
    conteudo.append(estoque.size.toString())
    val nomeDoArquivo = "PRECO_M$cnpj.TXT"

    val file = File(diretorio.diretorioEstoqueLocal + nomeDoArquivo)
    file.writeText(conteudo.toString())
    return file
}

fun geraEstoqueCloseUp(cnpj: String, repository: CloseUpRepository, diretorio: Diretorio): File {
    val format = DateTimeFormatter.ofPattern("ddMMyyyy")
    val estoqueCloseup = Estoque()
    estoqueCloseup.cnpjDistribuidor = cnpj
    estoqueCloseup.dataPosicaoEstoque = LocalDateTime.now().format(format)


    val estoque = repository.findAll()
    var conteudo = StringBuilder()

    conteudo.append(estoqueCloseup.tipoRegistro)
    conteudo.append(cnpj)
    conteudo.append(estoqueCloseup.dataPosicaoEstoque)
    conteudo.append(LocalTime.now().format(DateTimeFormatter.ofPattern("HHmm")))
    conteudo.append("\n")


    estoque.forEach {
        conteudo.append(it.tipoRegistro)
        conteudo.append(it.codigoBarras)
        conteudo.append(StringUtils.leftPad(it.quantidade.toString(),10, "0"))
        conteudo.append("\n")
    }
    val nomeDoArquivo = "ESTOQUE_M$cnpj.TXT"

    val file = File(diretorio.diretorioEstoqueLocal + nomeDoArquivo)
    file.writeText(conteudo.toString())
    return file
}