package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.ems.PedidoEMS
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.ems.ItemEMS
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate

@ActiveProfiles("test")
@SpringBootTest
internal class FinalizaMovimentoTest {
    @Autowired
    lateinit var finalizaMovimento: FinalizaMovimento
    @Autowired
    lateinit var bloqueioMovimentoRepository: BloqueioMovimentoRepository
    @Autowired
    lateinit var pedidoPalmRepository: PedidoPalmRepository

    @Test
    fun `dado um objeto, deve gerar um pedido e o movimento deve ser finalizado`(){
        val pedidoPalm = pedidoPalmRepository.save(generatePedidoEMS().toPedidoPalm())

        pedidoPalmRepository.toMovimento(pedidoPalm)
        val pedidoGeradoMovimento = pedidoPalmRepository.findById(pedidoPalm.IdPedidoPalm!!)

        bloqueioMovimentoRepository.executaRegrasTipoGravarRetornos(pedidoGeradoMovimento!!)

        val pedidoComStatusRetorno = pedidoPalmRepository.findById(pedidoPalm.IdPedidoPalm!!)

        val status = finalizaMovimento.finaliza(pedidoComStatusRetorno!!)




        assertThat(status, not(equalTo("N")))
    }

    private fun generatePedidoEMS(): PedidoEMS {
        return PedidoEMS(
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