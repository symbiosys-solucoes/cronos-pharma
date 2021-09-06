package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.consys.ItemCONSYS
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.consys.PedidoCONSYS
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.ems.EMS
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.ems.ItemEMS
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import java.math.BigDecimal
import java.time.LocalDate

@ActiveProfiles("test")
@SpringBootTest
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, statements = ["delete PedidoPalm where NumPedidoPalm in ('99999999','88888888')" ,"update Movimento set Status = 'C' where NumPedidoPalm = '88888888'", "update Movimento set Status = 'C' where NumPedidoPalm = '99999999'"])
internal class PedidoPalmTest {
    @Autowired
    lateinit var repository: PedidoPalmRepository
    @Autowired
    lateinit var repositoryBloqueioMovimento: BloqueioMovimentoRepository

    @Test
    fun `dado um pedido EMS deve retornar um PedidoPalm`(){
        val pedidoPalm = repository.save(generatePedidoEMS().toPedidoPalm())
        val item = pedidoPalm.itens.first()

        assertThat(pedidoPalm.itens, hasSize(1))
        assertThat(pedidoPalm.CodCliFor, equalTo("C20559"))
        assertThat(pedidoPalm.CodPortador, equalTo("07"))
        assertThat(pedidoPalm.CodVendedor, equalTo("21427"))
        assertThat(pedidoPalm.CodCondPag, equalTo("04"))

        assertThat(item.IdPedidoPalm, equalTo(pedidoPalm.IdPedidoPalm))
        assertThat(item.IdProduto, equalTo(42374))
        assertThat(item.CodProduto, equalTo("42374"))
        assertThat(item.Qtd, equalTo(3.0))
        assertThat(item.PrecoUnit?.setScale(2) ?:0, equalTo(BigDecimal("37.92")))


    }

    @Test
    fun `dado um pedido CONSYS deve retornar um PedidoPalm`(){
        val pedidoPalm = repository.save(generatePedidoConsys().toPedidoPalm())
        val item = pedidoPalm.itens.first()

        assertThat(pedidoPalm.itens, hasSize(1))
        assertThat(pedidoPalm.CodCliFor, equalTo("C20559"))
        assertThat(pedidoPalm.CodPortador, equalTo("07"))
        assertThat(pedidoPalm.CodVendedor, equalTo("21427"))
        assertThat(pedidoPalm.CodCondPag, equalTo("04"))

        assertThat(item.IdPedidoPalm, equalTo(pedidoPalm.IdPedidoPalm))
        assertThat(item.IdProduto, equalTo(42374))
        assertThat(item.CodProduto, equalTo("42374"))
        assertThat(item.Qtd, equalTo(5.0))
        assertThat(item.PrecoUnit?.setScale(2) ?:0, equalTo(BigDecimal("37.92")))


    }

    @Test
    fun `dado um PedidoPalm deve criar um Movimento no CRONOS`(){
        val pedidoPalm = repository.save(generatePedidoConsys().toPedidoPalm())

        val nummov = repository.toMovimento(pedidoPalm)

        val pedidoGeradoMovimento = repository.findById(pedidoPalm.IdPedidoPalm!!)

        if (pedidoGeradoMovimento != null){
            assertThat(pedidoGeradoMovimento.SituacaoPedido, equalTo("C") )
            assertThat(pedidoGeradoMovimento.NumPedidoCRONOS, notNullValue())
        }



    }

    @Test
    fun `dado um PedidoPalm EMS deve gravar o status de retorno`(){
        val pedidoPalm = repository.save(generatePedidoEMS().toPedidoPalm())

        repository.toMovimento(pedidoPalm)
        val pedidoGeradoMovimento = repository.findById(pedidoPalm.IdPedidoPalm!!)

        repositoryBloqueioMovimento.executaRegrasTipoGravarRetornos(pedidoGeradoMovimento!!)

        val pedidoComStatusRetorno = repository.findById(pedidoPalm.IdPedidoPalm!!)
        println(pedidoComStatusRetorno)

        assertThat(pedidoComStatusRetorno!!.CodRetorno, `in`(listOf("50","51","52")))
    }


    private fun generatePedidoConsys(): PedidoCONSYS{
        return PedidoCONSYS(
            cnpj = "13197261000357",
            cliente = "20559",
            idPedido = "88888888",
            dataPedido = LocalDate.now(),
            produtos = listOf(
                ItemCONSYS(
                    codigo = "42374",
                    quantidade = 5.0,
                    codigoCONSYS = "123456"
                )
            )

        )
    }
    private fun generatePedidoEMS(): EMS{
        return EMS(
            codigoCliente = "13197261000357",
            numeroPedido = "99999999",
            dataPedido = LocalDate.now(),
            numeroPedidoCliente = "LEG585786",
            codigoRepresentante = "3009",
            produtos = listOf(
                ItemEMS(
                   codigoProduto = "7894916142632",
                   quantidade = 3.0,
                   desconto = 57.72
                )
            )

        )
    }
}