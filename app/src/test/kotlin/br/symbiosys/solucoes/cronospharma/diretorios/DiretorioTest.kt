package br.symbiosys.solucoes.cronospharma.diretorios

import br.symbiosys.solucoes.cronospharma.entidades.TipoIntegracao
import br.symbiosys.solucoes.cronospharma.entidades.diretorios.DiretoriosRepository
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.collection.IsCollectionWithSize.hasSize
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
internal class DiretorioTest {

    @Autowired
    lateinit var repository: DiretoriosRepository

    @Test
    fun `deve retornar uma lista de Diretorios`() {

        val listDiretorios = repository.findAll()

        assertThat(listDiretorios, hasSize(1))
        assertThat(listDiretorios.first().url, equalTo("localhost"))
        assertThat(listDiretorios.first().tipoIntegracao, equalTo(TipoIntegracao.EMS))
    }

}