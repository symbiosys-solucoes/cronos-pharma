package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.ems.EMS
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.ems.ItemEMS
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate

@ActiveProfiles("test")
@SpringBootTest
internal class PedidoPalmTest {
    @Autowired
    lateinit var repository: PedidoPalmRepository

    @Test
    fun `dado um pedido EMS deve retornar um PedidoPalm`(){
        val pedidoPalm = repository.save(generatePedidoEMS().toPedidoPalm())

        assertThat(pedidoPalm.itens, hasSize(1))
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