package br.symbiosys.solucoes.cronospharma.cronospharma.entidades

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos.ItemPedidoPalm
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos.PedidoPalm
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.diretorios.Diretorio
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.ems.EMS
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.ems.ItemEMS
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.StringContains.containsString

import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.nio.file.Paths
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.io.path.Path
import kotlin.io.path.pathString


internal class RetornoTest {

    @Test
    fun `dado um PedidoPalm deve gerar um arquivo de retorno`(){
        val data = LocalDateTime.now().toString()
        val retorno = generateEms().gerarRetorno("01260848000112",generatePedidoPalm(), generateDiretorio())
        val caminho = Path(generateDiretorio().diretorioRetornoLocal+ "RETEMS_01260848000112_${data.substring(0,4)}${data.substring(5,7)}${data.substring(8,10)}${data.substring(11,13)}${data.substring(14,16)}${data.substring(17,19)}")
        var conteudo = StringBuilder()
        retorno.bufferedReader().forEachLine { conteudo.append(it+"\n") }
        println(conteudo)
        assertThat(retorno.path, containsString(caminho.pathString))

    }

    private fun generatePedidoPalm(): PedidoPalm{

        return PedidoPalm(
            Origem = "EMS",
            IdPedidoPalm = 25562,
            NumPedidoPalm = "19875141",
            IdUsuario = "dbo",
            DataOperacao = LocalDateTime.now(),
            LogImportacao = null,
            NumPedidoPalmAux = null,
            IdEmpresa = 1,
            ArqRet2Ped = null,
            CodFilial = "01",
            VerLayout = null,
            DscRetorno = "PEDIDO CONCLUÍDO COM SUCESSO",
            CodRetorno = "50",
            ArqRetPed = null,
            StatusRetorno = null,
            NumPedidoCRONOS = "151163478",
            CodVendedor = "21427",
            CodCliFor = "C00252",
            DataPedido = LocalDateTime.of(2020, 9, 8, 0, 0,0,0),
            PercComissao = null,
            CodCondPag = "07",
            CodPortador = "01",
            PercDesconto = 0.0,
            TotalPedido = BigDecimal("359.20"),
            DataEntrega = null,
            Observacoes = null,
            IdExpedicao = null,
            DataProxVisita = null,
            SituacaoPedido = "C",
            NumNF = null,
            CnpjCpfCliFor = "12754107000104",
            ArqRetNF = null,
            itens = listOf(
                ItemPedidoPalm(IdItemPedidoPalm = 878768, IdPedidoPalm = 25562, Item = 1, IdEmpresa = 1, IdProduto = 42374,
                CodProduto = "42374", Qtd = 5.0, QtdConfirmada = 5.0, IdPrecoTabela = "1", PrecoUnit = BigDecimal("37.92"),
                PercDescontoItem = 60.0, PercComissaoItem = null, SituacaoItemPedido = "C", LogImportacao = null, IdUsuario = "dbo",
                DataOperacao = LocalDateTime.of(2020, 9, 8, 16, 50, 50, 21), CodRetornoItem = "13", DscRetornoItem = "PRODUTO FATURADO",
                CodProdutoArq = "7894916142632"),
                ItemPedidoPalm(IdItemPedidoPalm = 878769, IdPedidoPalm = 25562, Item = 1, IdEmpresa = 1, IdProduto = 32034,
                    CodProduto = "32034", Qtd = 6.0, QtdConfirmada = 5.0, IdPrecoTabela = "1", PrecoUnit = BigDecimal("17.57"),
                    PercDescontoItem = 75.0, PercComissaoItem = null, SituacaoItemPedido = "I", LogImportacao = null, IdUsuario = "dbo",
                    DataOperacao = LocalDateTime.of(2020, 9, 8, 16, 50, 50, 22), CodRetornoItem = "14", DscRetornoItem = "PRODUTO ATENDIDO PARCIALMENTE",
                    CodProdutoArq = "7894916143295"),
                ItemPedidoPalm(IdItemPedidoPalm = 878770, IdPedidoPalm = 25562, Item = 1, IdEmpresa = 1, IdProduto = 56197,
                    CodProduto = "56197", Qtd = 2.0, QtdConfirmada = 2.0, IdPrecoTabela = "1", PrecoUnit = BigDecimal("32.09"),
                    PercDescontoItem = 20.0, PercComissaoItem = null, SituacaoItemPedido = "C", LogImportacao = null, IdUsuario = "dbo",
                    DataOperacao = LocalDateTime.of(2020, 9, 8, 16, 50, 50, 23), CodRetornoItem = "13", DscRetornoItem = "PRODUTO FATURADO",
                    CodProdutoArq = "7894916144971"),
            )

        )
    }

    private fun generateDiretorio(): Diretorio {
        return Diretorio(
            TipoIntegracao.EMS,
            usaFTP = true,
            ativo = true,
            url = "localhost",
            login = "ems",
            senha = "123456",
            diretorioPedidoFTP = "/ped/",
            diretorioRetornoFTP = "/ret/",
            diretorioPedidoLocal = "C:\\Symbiosys\\ped\\",
            diretorioRetornoLocal = "C:\\Symbiosys\\ret\\",
            diretorioImportadosLocal = "C:\\Symbiosys\\imp\\",

        )
    }

    private fun generateEms(): EMS{
        return EMS(
            codigoCliente = "12754107000104",
            numeroPedido = "19875141",
            dataPedido = LocalDate.of(2020, 9, 8),
            produtos = listOf(
                ItemEMS( codigoProduto = "7894916142632", quantidade = 5.0, desconto = 60.0),
                ItemEMS( codigoProduto = "7894916143295", quantidade = 6.0, desconto = 75.0),
                ItemEMS( codigoProduto = "7894916144971", quantidade = 2.0, desconto = 20.0)
            )
        )
    }
}