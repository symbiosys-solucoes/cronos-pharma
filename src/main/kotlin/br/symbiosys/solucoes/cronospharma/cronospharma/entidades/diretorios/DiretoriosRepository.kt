package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.diretorios

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.TipoIntegracao
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class DiretoriosRepository (
    private val jdbcTemplate: NamedParameterJdbcTemplate
    ){

    fun findAll(): List<Diretorio> {

        return jdbcTemplate.query(sqlDiretorios,mapperDiretorio)
    }

    private val mapperDiretorio = RowMapper<Diretorio> { rs: ResultSet, rowNum: Int ->
        Diretorio(
            tipoIntegracao = TipoIntegracao.valueOf(rs.getString("CodTd_sym_tipo")),
            usaFTP = rs.getBoolean("sym_usaFTP"),
            ativo = rs.getBoolean("sym_ativo"),
            url = rs.getString("sym_url"),
            login = rs.getString("sym_login"),
            senha = rs.getString("sym_senha"),
            diretorioPedidoFTP = rs.getString("sym_dir_ped_ftp"),
            diretorioRetornoFTP = rs.getString("sym_dir_ret_ftp"),
            diretorioImportadosLocal = rs.getString("sym_dir_imp_local"),
            diretorioPedidoLocal = rs.getString("sym_dir_ped_local"),
            diretorioRetornoLocal = rs.getString("sym_dir_ret_local"),
            diretorioEstoqueFTP = rs.getString("sym_dir_est_ftp"),
            diretorioEstoqueLocal = rs.getString("sym_dir_est_local")

        )
    }
    private val sqlDiretorios: String = "SELECT \n" +
            "sym_ativo,\n" +
            "sym_usaFTP,\n" +
            "sym_url,\n" +
            "sym_login,\n" +
            "sym_senha,\n" +
            "sym_dir_ped_ftp,\n" +
            "sym_dir_ret_ftp,\n" +
            "sym_dir_ped_local,\n" +
            "sym_dir_ret_local,\n" +
            "sym_dir_imp_local,\n" +
            "CodTd_sym_tipo,\n" +
            "sym_dir_est_ftp,\n" +
            "sym_dir_est_local\n" +
            "FROM ZProdutosCompl WHERE sym_ativo = 'S'"
}