package br.symbiosys.solucoes.cronospharma.cronospharma.entidades

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.ems.EMS
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.time.LocalDate

internal class TipoIntegracaoTest {


    @Test
    fun `dado um arquivo EMS deve retornar um objeto EMS`(){
        val arquivo = File.createTempFile("PEDEMS_01260848000112_20200908124543","txt")
            arquivo.writeText(contentFileEms)

        val fileReader = FileReader(arquivo.path)
        val bufferedReader = BufferedReader(fileReader)

        val ems: EMS = TipoIntegracao.toEms(arquivo.path)


        assertThat(ems.codigoCliente, equalTo("13197261000357"))
        assertThat(ems.numeroPedido, equalTo("19873499"))
        assertThat(ems.dataPedido, equalTo(LocalDate.of(2020,9,8)))
        assertThat(ems.numeroPedidoCliente, equalTo("LEG585786"))
        assertThat(ems.codigoRepresentante, equalTo("3009"))
        assertThat(ems.produtos, hasSize(31))
        assertThat(ems.produtos?.first()?.codigoProduto ?: "Prdouto Nulo", equalTo("7894916142632"))
        assertThat(ems.produtos?.first()?.quantidade ?: "Prdouto Nulo", equalTo(1.0))
        assertThat(ems.produtos?.first()?.desconto ?: "Prdouto Nulo", equalTo(57.72))
        assertThat(ems.produtos?.first()?.prazo ?: "Prdouto Nulo", equalTo("000"))

    }

    private final val contentFileEms =
            "0PEDIDO OPER.LOG01260848000112 202009080000000057507378000365 \n" +
            "101319726100035719873499    08092020IC00000LEG585786      3009\n" +
            "40014820/30/40                      00300000020030040000000000033330333303334000000000000000\n" +
            "219873499    78949161426320000105772000ZZ 1\n" +
            "219873499    78949161438680000107826000ZZ 2\n" +
            "219873499    78960047043950000204661000ZZ 3\n" +
            "219873499    78949161456950000207343000ZZ 4\n" +
            "219873499    78949161434310000206859000ZZ 5\n" +
            "219873499    78960048101880001205531000ZZ 6\n" +
            "219873499    78960048102250000107463000ZZ 7\n" +
            "219873499    78960047257720000107926000ZZ 8\n" +
            "219873499    78949161454590000107947000ZZ 9\n" +
            "219873499    78949161451140000107584000ZZ 10\n" +
            "219873499    78949161451210000307584000ZZ 11\n" +
            "219873499    78960047150320000406739000ZZ 12\n" +
            "219873499    78949161481080000406300000ZZ 13\n" +
            "219873499    78949161456880000207826000ZZ 14\n" +
            "219873499    78960047567210000104661000ZZ 15\n" +
            "219873499    78960047684650000206859000ZZ 16\n" +
            "219873499    78960047684720000206859000ZZ 17\n" +
            "219873499    78949161430280004808309000ZZ 18\n" +
            "219873499    78949161407060000308973000ZZ 19\n" +
            "219873499    78949161406900000208792000ZZ 20\n" +
            "219873499    78960047341320000103477000ZZ 21\n" +
            "219873499    78960047226890000206859000ZZ 22\n" +
            "219873499    78949161404230000208973000ZZ 23\n" +
            "219873499    78949161411230000407100000ZZ 24\n" +
            "219873499    78960047078460001606618000ZZ 25\n" +
            "219873499    78960047208380000906135000ZZ 26\n" +
            "219873499    78960047152300000506135000ZZ 27\n" +
            "219873499    78960047039540002006859000ZZ 28\n" +
            "219873499    78949161479720000407947000ZZ 29\n" +
            "219873499    78960047600630000204000000ZZ 30\n" +
            "219873499    78960047390830000101544000ZZ 31\n" +
            "319873499    000310000000161\n"
}