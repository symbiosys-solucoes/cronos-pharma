package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos

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

    fun finaliza(pedidoPalm: PedidoPalm): String?{

        if(pedidoPalm.IdPedidoPalm == null || pedidoPalm.SituacaoPedido != "C") {
            throw SQLException("Erro IdPedido esta nulo")
        }
        val idmov = jdbcTemplate.queryForObject(idmovByIdPedidoPalm, MapSqlParameterSource("idPedido", pedidoPalm.IdPedidoPalm), Long::class.java)

        val listaResultados = idmov?.let { bloqueioMovimentoRepository.executaRegrasMovimento(it) }

        listaResultados!!.forEach {
            if (it.ok == "S" && it.tipo == "COMERCIAL") {
                jdbcTemplate.queryForObject("UPDATE Movimento SET StatusSeparacao = 'B', BloqueioComercial = 'S' OUTPUT INSERTED.BloqueioComercial WHERE IdMov = :id", MapSqlParameterSource("id", idmov), String::class.java)
            }
            if (it.ok == "S" && it.tipo == "FINANCEIRO") {
                jdbcTemplate.queryForObject("UPDATE Movimento SET StatusSeparacao = 'B', BloqueioFinanceiro = 'S' OUTPUT INSERTED.BloqueioFinanceiro WHERE IdMov = :id", MapSqlParameterSource("id", idmov), String::class.java)
            }
        }


        val statusSeparacao = jdbcTemplate.queryForObject(paramRules, MapSqlParameterSource("id", idmov), String::class.java)

        return statusSeparacao
    }

    companion object{
        private final val idmovByIdPedidoPalm = "select IdMov from Movimento where IdPedidoPalm = :idPedido "
        private final val paramRules = "" +
               "DECLARE @IDMOV INT --, @COMBLOQ CHAR(1) = 'S', @FINBLOQ CHAR(1) ='S'\n" +
                "\tSET @IDMOV = :id \n" +
                "DECLARE @DESCITEM NUMERIC(15,6), @DESCMOV NUMERIC(15,6), @CONDDESC CHAR(1), @PRODDESC CHAR(1), @PROMODESC CHAR(1), @PARAMDESC CHAR(1), @CODCLIFOR VARCHAR(10), @SALDO NUMERIC(15,6), @LIMBLOQ CHAR(1), @TITVENC CHAR(1), @LISTANEGRA CHAR(1) \n" +
                "DECLARE @LIMITE TABLE (LimiteMax NUMERIC(10,2), LanNaoBaixados NUMERIC(10,2), ChqNaoCompensados NUMERIC(10,2), LanNaoBaixadosVencidos NUMERIC(10,2), ChqNaoCompensadosVencidos NUMERIC(10,2), PedidosPendentes NUMERIC(10,2), DevolucoesPendentes NUMERIC(10,2)) \n" +
                "                                     \n" +
                "\tSELECT @DESCMOV = PercDesconto FROM MOVIMENTO WHERE IdMov = @IDMOV \n" +
                "                                     \n" +
                "\tSELECT @CODCLIFOR = CODCLIFOR FROM Movimento WHERE IdMov = @IDMOV \n" +
                "                                     \n" +
                "\t-- DESCONTOS COMERCIAIS \n" +
                "\tIF EXISTS ( SELECT 1 FROM Movimento WHERE StatusSeparacao = 'N' AND IdUsuarioAutorizacaoCom IS NULL AND DataAutorizacaoCom IS NULL AND IdMov = @IDMOV ) \n" +
                "\t\tBEGIN \n" +
                "\t\t\t/* VERIFICA DESCONTO DA CONDICAO DE PAGAMENTO */ \n" +
                "\t\t\tIF EXISTS( \n" +
                "             SELECT 1 \n" +
                "             FROM itensmov im (NOLOCK), Produtos p (NOLOCK), Movimento m  (NOLOCK), CondPag \n" +
                "             WHERE im.IdMov = @IDMOV AND im.IdMov = m.IdMov AND (m.CODCONDPAG = CondPag.CodCondPag) \n" +
                "             AND im.IdProduto = p.IdProduto \n" +
                "             AND ISNULL(condpag.PercDescontoMaximo,0) > 0 AND ISNULL(CondPag.PercDescontoMaximo,0) < @DESCMOV + ISNULL(im.PercDescontoItem,0) ) \n" +
                "             \n" +
                "\t\t\t\tSELECT @CONDDESC = 'S' \n" +
                "                                     \n" +
                "            /* VERIFICA DESCONTO NO CADASTRO DO PRODUTO */ \n" +
                "            IF EXISTS( \n" +
                "             SELECT 1 FROM itensmov im (NOLOCK), Produtos p (NOLOCK), Movimento m  (NOLOCK) \n" +
                "             WHERE im.IdMov = @IDMOV  AND im.IdMov = m.IdMov \n" +
                "             AND im.IdProduto = p.IdProduto \n" +
                "             AND ISNULL(p.UtilizarDescCad,'N') = 'S' AND ISNULL(p.PercDescMax,0) < @DESCMOV + ISNULL(im.PercDescontoItem,0) ) \n" +
                "            \n" +
                "\t\t\t\tSELECT @PRODDESC = 'S' \n" +
                "                                     \n" +
                "            /* Verifica se tem desconto em Itens em Promocao */ \n" +
                "            IF EXISTS( \n" +
                "             SELECT 1 FROM itensmov im, Produtos p (NOLOCK), Movimento m  (NOLOCK) \n" +
                "             WHERE im.IdMov     = @IDMOV  AND im.IdMov = m.IdMov AND ISNULL(im.PercDescontoItem,0) > 0 \n" +
                "             AND im.IdProduto = p.IdProduto \n" +
                "             AND ISNULL(p.StatusProduto,'N') <> 'N' ) \n" +
                "            \n" +
                "\t\t\t\tSELECT @PROMODESC = 'S' \n" +
                "                                       \n" +
                "\t\t\t /* VERIFICA PARAMETROS DO MOVIMENTO */ \n" +
                "\t\t\tIF EXISTS( \n" +
                "\t\t\t\tSELECT 1 \n" +
                "\t\t\t\tFROM itensmov im (NOLOCK), Produtos p (NOLOCK), Movimento m  (NOLOCK), ParametrosMovimento pm \n" +
                "\t\t\t\tWHERE im.IdMov     = @IDMOV  AND im.IdMov = m.IdMov AND pm.TipoMov = m.TipoMov \n" +
                "\t\t\t\tAND im.IdProduto = p.IdProduto  AND ISNULL(p.UtilizarDescCad,'N') = 'N' \n" +
                "\t\t\t\tAND (CASE WHEN p.TipoProduto = 'S' THEN ISNULL(PercDescMaxSrv,0.00) ELSE ISNULL(PercDescMaxProd,0.00) END)  < @DESCMOV + ISNULL(im.PercDescontoItem,0)  \n" +
                "\t\t\t\tAND (CASE WHEN p.TipoProduto = 'S' THEN ISNULL(PercDescMaxSrv,0.00) ELSE ISNULL(PercDescMaxProd,0.00) END)  > 0 ) \n" +
                "\t\t\t\t\n" +
                "\t\t\t\t\tSELECT @PARAMDESC = 'S' \n" +
                "                                     \n" +
                "                                     \n" +
                "\t\t\t\t -- SELECT @CONDDESC, @PRODDESC, @PROMODESC, @PARAMDESC \n" +
                "                                     \n" +
                "\t\t\t\tUPDATE Movimento SET ObsBloqueioComercial = ObsBloqueioComercial + '*DESCONTO ULTRAPASSA O LIMITE DA CONDICAO DE PAGAMENTO - SYM' + CHAR(13) + CHAR(10) \n" +
                "\t\t\t\tFROM MOVIMENTO WHERE IdMov = @IDMOV AND @CONDDESC = 'S' \n" +
                "                                     \n" +
                "\t\t\t\tUPDATE Movimento SET ObsBloqueioComercial = ObsBloqueioComercial + '*DESCONTO ULTRAPASSA O LIMITE DO CADASTRO DE PRODUTO - SYM' + CHAR(13) + CHAR(10)\n" +
                "\t\t\t\tFROM MOVIMENTO WHERE IdMov = @IDMOV AND @PRODDESC = 'S' \n" +
                "                                     \n" +
                "\t\t\t\tUPDATE Movimento SET ObsBloqueioComercial = ObsBloqueioComercial + '*PRODUTO EM PROMOCAO NAO PODE TER DESCONTO - SYM'  + CHAR(13) + CHAR(10)\n" +
                "\t\t\t\tFROM MOVIMENTO WHERE IdMov = @IDMOV AND @PROMODESC = 'S' \n" +
                "                                     \n" +
                "\t\t\t\tUPDATE Movimento SET ObsBloqueioComercial =  ObsBloqueioComercial + '*DESCONTO ULTRAPASSA O LIMITE DO MOVIMENTO - SYM'  + CHAR(13) + CHAR(10)\n" +
                "\t\t\t\tFROM MOVIMENTO WHERE IdMov = @IDMOV AND @PARAMDESC = 'S' \n" +
                "                                     \n" +
                "\t\t\t\t--IF EXISTS (SELECT @CONDDESC, @PRODDESC, @PROMODESC, @PARAMDESC ) \n" +
                "\t\t\t\t--UPDATE Movimento SET StatusSeparacao = 'B', BloqueioComercial = 'S' WHERE IdMov = @IDMOV \n" +
                "\t\t\t\tUPDATE Movimento SET StatusSeparacao = CASE \n" +
                "\t\t\t\t\tWHEN ISNULL(@LIMBLOQ,'N') = 'S' THEN 'B' \n" +
                "\t\t\t\t\tWHEN ISNULL(@TITVENC,'N') = 'S' THEN 'B' \n" +
                "\t\t\t\t\tWHEN ISNULL(@LISTANEGRA,'N') = 'S' THEN 'B' \n" +
                "\t\t\t\t\tELSE StatusSeparacao END, \n" +
                "                                     \n" +
                "\t\t\t\tBloqueioComercial = CASE WHEN ISNULL(@LIMBLOQ,'N') = 'S' THEN 'S' \n" +
                "                                       WHEN ISNULL(@TITVENC,'N') = 'S' THEN 'S' \n" +
                "                                       WHEN ISNULL(@LISTANEGRA,'N') = 'S' THEN 'S' \n" +
                "                                       ELSE 'N' END \n" +
                "                                        \n" +
                "                                       WHERE IdMov = @IDMOV \n" +
                "                                     \n" +
                "               --------------------------------------------------------------------------- \n" +
                "                                     \n" +
                "               -- REGRAS FINANCEIRAS \n" +
                "                                     \n" +
                "               /* VERIFICANDO LIMITE DE CREDITO DO CLIENTE */ \n" +
                "                                     \n" +
                "               INSERT INTO @LIMITE \n" +
                "                                     \n" +
                "               exec [dbo].[sp_LimiteCredito] @CODCLIFOR,1 \n" +
                "                                     \n" +
                "               SELECT @SALDO = LimiteMax - LanNaoBaixados - ChqNaoCompensados - LanNaoBaixadosVencidos - PedidosPendentes + DevolucoesPendentes FROM @LIMITE \n" +
                "                                     \n" +
                "               IF (@SALDO <=0) \n" +
                "               SELECT @LIMBLOQ = 'S' \n" +
                "                                     \n" +
                "               /* VERIFICANDO TITULOS EM ATRASO */ \n" +
                "                                     \n" +
                "               IF ((SELECT LanNaoBaixadosVencidos FROM @LIMITE) > 0) \n" +
                "\t\t\t\tSELECT @TITVENC = 'S' \n" +
                "                                     \n" +
                "               /* VERIFICANDO SE ESTA NA LISTA NEGRA */ \n" +
                "                                     \n" +
                "               IF ((SELECT ISNULL(LISTANEGRA,'N') FROM Cli_For WHERE CodCliFor = @CODCLIFOR) = 'S') \n" +
                "               SELECT @LISTANEGRA = 'S' \n" +
                "                                     \n" +
                "                                     \n" +
                "               UPDATE MOVIMENTO SET ObsBloqueioFinanceiro = ObsBloqueioFinanceiro + 'CLIENTE SEM LIMITE DISPONIVEL - SYM' \n" +
                "               WHERE IdMov = @IDMOV AND @LIMBLOQ = 'S' \n" +
                "                                     \n" +
                "               UPDATE MOVIMENTO SET ObsBloqueioFinanceiro = ObsBloqueioFinanceiro + 'CLIENTE COM TITULO EM ATRASO - SYM' \n" +
                "               WHERE IdMov = @IDMOV AND @TITVENC = 'S' \n" +
                "                                      \n" +
                "               UPDATE MOVIMENTO SET ObsBloqueioFinanceiro = ObsBloqueioFinanceiro + 'CLIENTE NA LISTA NEGRA - SYM' \n" +
                "               WHERE IdMov = @IDMOV AND @LISTANEGRA = 'S'  \n" +
                "                                     \n" +
                "                                     \n" +
                "               --IF EXISTS (SELECT @LIMBLOQ, @TITVENC, @LISTANEGRA ) \n" +
                "               UPDATE Movimento SET StatusSeparacao = CASE WHEN ISNULL(@LIMBLOQ,'N') = 'S' THEN 'B' \n" +
                "                                       WHEN ISNULL(@TITVENC,'N') = 'S' THEN 'B' \n" +
                "                                       WHEN ISNULL(@LISTANEGRA,'N') = 'S' THEN 'B' \n" +
                "                                       ELSE StatusSeparacao END, \n" +
                "                                     \n" +
                "                                     BloqueioFinanceiro = CASE WHEN ISNULL(@LIMBLOQ,'N') = 'S' THEN 'S' \n" +
                "                                       WHEN ISNULL(@TITVENC,'N') = 'S' THEN 'S' \n" +
                "                                       WHEN ISNULL(@LISTANEGRA,'N') = 'S' THEN 'S' \n" +
                "                                       ELSE 'N' END \n" +
                "                                        \n" +
                "                                       WHERE IdMov = @IDMOV \n" +
                "                                     \n" +
                "                                     \n" +
                "                                     \n" +
                "                END  \n" +
                "                SELECT STATUSSEPARACAO FROM MOVIMENTO WHERE IDMOV = @IDMOV  \n" +
                "                                    "
    }
}