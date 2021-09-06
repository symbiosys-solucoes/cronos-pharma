package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.collection.IsCollectionWithSize.hasSize
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@SpringBootTest
internal class BloqueioMovimentoTest{
    @Autowired
    lateinit var repository: BloqueioMovimentoRepository

    @Test
    fun `dado um tipo de BloqueioMovimento deve retornar uma lista de BloqueioMovimento`(){
        val bloqueios = repository.findByTipoBloqueio("023")

        assertThat(bloqueios, hasSize(5))
    }


}