package br.symbiosys.solucoes.cronospharma.cronospharma.controllers.testes

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.TipoIntegracao
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos.PedidoPalmRepository
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.medquimica.Pedido
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/testes")
class TestesController {

    @Autowired
    private lateinit var pedidoPalmRepository: PedidoPalmRepository


    @PostMapping
    fun testMedquimica(@RequestBody caminho: String): Any {
        var pedido = TipoIntegracao.toMedquimica(caminho).toPedidoPalm()
        return pedidoPalmRepository.save(pedido)



    }
}