package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.SQLException

@Repository
class FinalizaMovimento(
    private val jdbcTemplate: NamedParameterJdbcTemplate

) {
    @Autowired
    private lateinit var bloqueioMovimentoRepository: BloqueioMovimentoRepository

    private val logger = LoggerFactory.getLogger(FinalizaMovimento::class.java)

    fun finaliza(pedidoPalm: PedidoPalm): String?{

        if(pedidoPalm.IdPedidoPalm == null || pedidoPalm.SituacaoPedido != "C") {
            throw SQLException("Erro IdPedido esta nulo")
        }
        val idmov = jdbcTemplate.queryForObject(idmovByIdPedidoPalm, MapSqlParameterSource("idPedido", pedidoPalm.IdPedidoPalm), Long::class.java)
        println("esse é o idmov encontrado: $idmov")
        val listaResultados = idmov?.let { bloqueioMovimentoRepository.executaRegrasMovimento(it) }

        listaResultados!!.forEach {
            if (it.ok == "S" && it.tipo == "COMERCIAL") {
                jdbcTemplate.queryForObject("UPDATE Movimento SET StatusSeparacao = 'B', BloqueioComercial = 'S'" +
                        "WHERE IdMov = :id\n" +
                        "SELECT BloqueioComercial FROM Movimento WHERE IdMov = :id ", MapSqlParameterSource("id", idmov), String::class.java)
            }
            if (it.ok == "S" && it.tipo == "FINANCEIRO") {
                jdbcTemplate.queryForObject("UPDATE Movimento SET StatusSeparacao = 'B', BloqueioFinanceiro = 'S' " +
                        "WHERE IdMov = :id\n" +
                        "SELECT BloqueioFinanceiro FROM Movimento WHERE IdMov = :id ", MapSqlParameterSource("id", idmov), String::class.java)
            }
            if (it.ok == "S" && it.tipo == "FINALIZACAO"){
                jdbcTemplate.queryForObject("DECLARE @IDMOV INT\n" +
                        "SET @IDMOV = :id\n" +
                        "IF EXISTS(SELECT 1 FROM ZMovimentoCompl WHERE IdMov = @IDMOV)\n" +
                        "\tBEGIN\n" +
                        "\t\tUPDATE ZMovimentoCompl  set sym_deveFinalizar = 'S' OUTPUT inserted.sym_deveFinalizar WHERE IdMov = @IDMOV\n" +
                        "\tEND\n" +
                        "ELSE\n" +
                        "\tBEGIN\n" +
                        "\t\tINSERT INTO ZMovimentoCompl (IdMov, sym_deveFinalizar) OUTPUT inserted.sym_deveFinalizar VALUES (@IDMOV, 'S')\n" +
                        "\tEND", MapSqlParameterSource("id", idmov), String::class.java)
            }
        }


        val statusSeparacao = jdbcTemplate.queryForObject(paramRules, MapSqlParameterSource("id", idmov), String::class.java)
        when (statusSeparacao) {
            "N" -> logger.info("O Movimento não foi finalizado, pois alguma regra de négocio impediu a finalização")
            "B" -> logger.info("O Movimento foi atualizado com status Bloqueado, pois não passou em alguma regra")
            else -> logger.info("O Movimento foi finalizado com sucesso")
        }
        return statusSeparacao
    }

    companion object{
        private val idmovByIdPedidoPalm = "select IdMov from Movimento where IdPedidoPalm = :idPedido "
        private val paramRules = "" +
                "DECLARE @IDMOV INT --, @COMBLOQ CHAR(1) = 'S', @FINBLOQ CHAR(1) ='S'\n" +
                "\n" +
                "SET @IDMOV = :id \n" +
                "\n" +
                "DECLARE @DESCITEM   NUMERIC(15, 6),\n" +
                "        @DESCMOV    NUMERIC(15, 6),\n" +
                "        @CONDDESC   CHAR(1),\n" +
                "        @PRODDESC   CHAR(1),\n" +
                "        @PROMODESC  CHAR(1),\n" +
                "        @PARAMDESC  CHAR(1),\n" +
                "        @CODCLIFOR  VARCHAR(10),\n" +
                "        @SALDO      NUMERIC(15, 6),\n" +
                "        @LIMBLOQ    CHAR(1),\n" +
                "        @TITVENC    CHAR(1),\n" +
                "        @LISTANEGRA CHAR(1)\n" +
                "DECLARE @LIMITE TABLE\n" +
                "  (\n" +
                "     limitemax                 NUMERIC(10, 2),\n" +
                "     lannaobaixados            NUMERIC(10, 2),\n" +
                "     chqnaocompensados         NUMERIC(10, 2),\n" +
                "     lannaobaixadosvencidos    NUMERIC(10, 2),\n" +
                "     chqnaocompensadosvencidos NUMERIC(10, 2),\n" +
                "     pedidospendentes          NUMERIC(10, 2),\n" +
                "     devolucoespendentes       NUMERIC(10, 2)\n" +
                "  )\n" +
                "\n" +
                "SELECT @DESCMOV = percdesconto\n" +
                "FROM   movimento\n" +
                "WHERE  idmov = @IDMOV\n" +
                "\n" +
                "SELECT @CODCLIFOR = codclifor\n" +
                "FROM   movimento\n" +
                "WHERE  idmov = @IDMOV\n" +
                "\n" +
                "-- DESCONTOS COMERCIAIS \n" +
                "IF EXISTS (SELECT 1\n" +
                "           FROM   movimento\n" +
                "           WHERE  statusseparacao = 'N'\n" +
                "                  AND idusuarioautorizacaocom IS NULL\n" +
                "                  AND dataautorizacaocom IS NULL\n" +
                "                  AND ISNULL((SELECT sym_deveFinalizar FROM ZMovimentoCompl WHERE IdMov = @IDMOV),'N') = 'N'\n" +
                "                  AND idmov = @IDMOV)\n" +
                "  BEGIN\n" +
                "      /* VERIFICA DESCONTO DA CONDICAO DE PAGAMENTO */\n" +
                "      IF EXISTS(SELECT 1\n" +
                "                FROM   itensmov im (nolock),\n" +
                "                       produtos p (nolock),\n" +
                "                       movimento m (nolock),\n" +
                "                       condpag\n" +
                "                WHERE  im.idmov = @IDMOV\n" +
                "                       AND im.idmov = m.idmov\n" +
                "                       AND ( m.codcondpag = condpag.codcondpag )\n" +
                "                       AND im.idproduto = p.idproduto\n" +
                "                       AND Isnull(condpag.percdescontomaximo, 0) > 0\n" +
                "                       AND Isnull(condpag.percdescontomaximo, 0) <\n" +
                "                           @DESCMOV + Isnull(im.percdescontoitem, 0))\n" +
                "        BEGIN\n" +
                "            SELECT @CONDDESC = 'S'\n" +
                "\n" +
                "            UPDATE movimento\n" +
                "            SET    obsbloqueiocomercial = Isnull(obsbloqueiocomercial, '')\n" +
                "                                          +\n" +
                "            '*DESCONTO ULTRAPASSA O LIMITE DA CONDICAO DE PAGAMENTO - SYM'\n" +
                "                            + Char(13) + Char(10)\n" +
                "            FROM   movimento\n" +
                "            WHERE  idmov = @IDMOV\n" +
                "        END\n" +
                "\n" +
                "      /* VERIFICA DESCONTO NO CADASTRO DO PRODUTO */\n" +
                "      IF EXISTS(SELECT 1\n" +
                "                FROM   itensmov im (nolock),\n" +
                "                       produtos p (nolock),\n" +
                "                       movimento m (nolock)\n" +
                "                WHERE  im.idmov = @IDMOV\n" +
                "                       AND im.idmov = m.idmov\n" +
                "                       AND im.idproduto = p.idproduto\n" +
                "                       AND Isnull(p.utilizardesccad, 'N') = 'S'\n" +
                "                       AND Isnull(p.percdescmax, 0) <\n" +
                "                           @DESCMOV + Isnull(im.percdescontoitem, 0))\n" +
                "        BEGIN\n" +
                "            SELECT @PRODDESC = 'S'\n" +
                "\n" +
                "            UPDATE movimento\n" +
                "            SET    obsbloqueiocomercial = Isnull(obsbloqueiocomercial, '')\n" +
                "                                          +\n" +
                "            '*DESCONTO ULTRAPASSA O LIMITE DO CADASTRO DE PRODUTO - SYM'\n" +
                "                            + Char(13) + Char(10)\n" +
                "            FROM   movimento\n" +
                "            WHERE  idmov = @IDMOV\n" +
                "        END\n" +
                "\n" +
                "      /* Verifica se tem desconto em Itens em Promocao */\n" +
                "      IF EXISTS(SELECT 1\n" +
                "                FROM   itensmov im,\n" +
                "                       produtos p (nolock),\n" +
                "                       movimento m (nolock)\n" +
                "                WHERE  im.idmov = @IDMOV\n" +
                "                       AND im.idmov = m.idmov\n" +
                "                       AND Isnull(im.percdescontoitem, 0) > 0\n" +
                "                       AND im.idproduto = p.idproduto\n" +
                "                       AND Isnull(p.statusproduto, 'N') <> 'N')\n" +
                "        BEGIN\n" +
                "            SELECT @PROMODESC = 'S'\n" +
                "\n" +
                "            UPDATE movimento\n" +
                "            SET    obsbloqueiocomercial = Isnull(obsbloqueiocomercial, '')\n" +
                "                                          +\n" +
                "                   '*PRODUTO EM PROMOCAO NAO PODE TER DESCONTO - SYM'\n" +
                "                                          + Char(13) + Char(10)\n" +
                "            FROM   movimento\n" +
                "            WHERE  idmov = @IDMOV\n" +
                "        END\n" +
                "\n" +
                "      /* VERIFICA PARAMETROS DO MOVIMENTO */\n" +
                "      IF EXISTS(SELECT 1\n" +
                "                FROM   itensmov im (nolock),\n" +
                "                       produtos p (nolock),\n" +
                "                       movimento m (nolock),\n" +
                "                       parametrosmovimento pm\n" +
                "                WHERE  im.idmov = @IDMOV\n" +
                "                       AND im.idmov = m.idmov\n" +
                "                       AND pm.tipomov = m.tipomov\n" +
                "                       AND im.idproduto = p.idproduto\n" +
                "                       AND Isnull(p.utilizardesccad, 'N') = 'N'\n" +
                "                       AND ( CASE\n" +
                "                               WHEN p.tipoproduto = 'S' THEN\n" +
                "                               Isnull(percdescmaxsrv, 0.00)\n" +
                "                               ELSE Isnull(percdescmaxprod, 0.00)\n" +
                "                             END ) < @DESCMOV + Isnull(im.percdescontoitem, 0)\n" +
                "                       AND ( CASE\n" +
                "                               WHEN p.tipoproduto = 'S' THEN\n" +
                "                               Isnull(percdescmaxsrv, 0.00)\n" +
                "                               ELSE Isnull(percdescmaxprod, 0.00)\n" +
                "                             END ) > 0)\n" +
                "        BEGIN\n" +
                "            SELECT @PARAMDESC = 'S'\n" +
                "\n" +
                "            UPDATE movimento\n" +
                "            SET    obsbloqueiocomercial = Isnull(obsbloqueiocomercial, '')\n" +
                "                                          +\n" +
                "                   '*DESCONTO ULTRAPASSA O LIMITE DO MOVIMENTO - SYM'\n" +
                "                                          + Char(13) + Char(10)\n" +
                "            FROM   movimento\n" +
                "            WHERE  idmov = @IDMOV\n" +
                "        END\n" +
                "\n" +
                "      UPDATE movimento\n" +
                "      SET    statusseparacao = CASE\n" +
                "                                 WHEN Isnull(@CONDDESC, 'N') = 'S' THEN 'B'\n" +
                "                                 WHEN Isnull(@PRODDESC, 'N') = 'S' THEN 'B'\n" +
                "                                 WHEN Isnull(@PROMODESC, 'N') = 'S' THEN 'B'\n" +
                "                                 WHEN Isnull(@PARAMDESC, 'N') = 'S' THEN 'B'\n" +
                "                                 ELSE statusseparacao\n" +
                "                               END,\n" +
                "             bloqueiocomercial = CASE\n" +
                "                                   WHEN Isnull(@CONDDESC, 'N') = 'S' THEN 'S'\n" +
                "                                   WHEN Isnull(@PRODDESC, 'N') = 'S' THEN 'S'\n" +
                "                                   WHEN Isnull(@PROMODESC, 'N') = 'S' THEN 'S'\n" +
                "                                   WHEN Isnull(@PARAMDESC, 'N') = 'S' THEN 'S'\n" +
                "                                   ELSE bloqueiocomercial\n" +
                "                                 END\n" +
                "      WHERE  idmov = @IDMOV\n" +
                "\n" +
                "      --------------------------------------------------------------------------- \n" +
                "      -- REGRAS FINANCEIRAS \n" +
                "      /* VERIFICANDO LIMITE DE CREDITO DO CLIENTE */\n" +
                "      INSERT INTO @LIMITE\n" +
                "      EXEC [dbo].[Sp_limitecredito]\n" +
                "        @CODCLIFOR,\n" +
                "        1\n" +
                "\n" +
                "      SELECT @SALDO = limitemax - lannaobaixados - chqnaocompensados\n" +
                "                      - lannaobaixadosvencidos\n" +
                "                                      - pedidospendentes + devolucoespendentes\n" +
                "      FROM   @LIMITE\n" +
                "\n" +
                "      IF ( @SALDO <= 0 )\n" +
                "        BEGIN\n" +
                "            SELECT @LIMBLOQ = 'S'\n" +
                "\n" +
                "            UPDATE movimento\n" +
                "            SET    obsbloqueiofinanceiro = Isnull(obsbloqueiofinanceiro, '')\n" +
                "                                           +\n" +
                "                   '*CLIENTE SEM LIMITE DISPONIVEL - SYM'\n" +
                "            WHERE  idmov = @IDMOV\n" +
                "        END\n" +
                "\n" +
                "      /* VERIFICANDO TITULOS EM ATRASO */\n" +
                "      IF ( (SELECT lannaobaixadosvencidos\n" +
                "            FROM   @LIMITE) > 0 )\n" +
                "        BEGIN\n" +
                "            SELECT @TITVENC = 'S'\n" +
                "\n" +
                "            UPDATE movimento\n" +
                "            SET    obsbloqueiofinanceiro = Isnull(obsbloqueiofinanceiro, '')\n" +
                "                                           +\n" +
                "                   '*CLIENTE COM TITULO EM ATRASO - SYM'\n" +
                "            WHERE  idmov = @IDMOV\n" +
                "        END\n" +
                "\n" +
                "      /* VERIFICANDO SE ESTA NA LISTA NEGRA */\n" +
                "      IF ( (SELECT Isnull(listanegra, 'N')\n" +
                "            FROM   cli_for\n" +
                "            WHERE  codclifor = @CODCLIFOR) = 'S' )\n" +
                "        BEGIN\n" +
                "            SELECT @LISTANEGRA = 'S'\n" +
                "\n" +
                "            UPDATE movimento\n" +
                "            SET    obsbloqueiofinanceiro = Isnull(obsbloqueiofinanceiro, '')\n" +
                "                                           + '*CLIENTE NA LISTA NEGRA - SYM'\n" +
                "            WHERE  idmov = @IDMOV\n" +
                "        END\n" +
                "\n" +
                "      UPDATE movimento\n" +
                "      SET    statusseparacao = CASE\n" +
                "                                 WHEN Isnull(@LIMBLOQ, 'N') = 'S' THEN 'B'\n" +
                "                                 WHEN Isnull(@TITVENC, 'N') = 'S' THEN 'B'\n" +
                "                                 WHEN Isnull(@LISTANEGRA, 'N') = 'S' THEN 'B'\n" +
                "                                 ELSE statusseparacao\n" +
                "                               END,\n" +
                "             bloqueiofinanceiro = CASE\n" +
                "                                    WHEN Isnull(@LIMBLOQ, 'N') = 'S' THEN 'S'\n" +
                "                                    WHEN Isnull(@TITVENC, 'N') = 'S' THEN 'S'\n" +
                "                                    WHEN Isnull(@LISTANEGRA, 'N') = 'S' THEN 'S'\n" +
                "                                    ELSE bloqueiofinanceiro\n" +
                "                                  END\n" +
                "      WHERE  idmov = @IDMOV\n" +
                "  END\n" +
                "\n" +
                "SELECT statusseparacao\n" +
                "FROM   movimento\n" +
                "WHERE  idmov = @IDMOV\n" +
                "--                              ',N'@P0 bigint',2109911"
    }
}