package br.symbiosys.solucoes.cronospharma.cronospharma.controllers.testes

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.TipoIntegracao
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos.BloqueioMovimentoRepository
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos.FinalizaMovimento
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos.PedidoPalmRepository
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.api.ApiPetronasAuth
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.services.AccountsService
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.services.OrderService
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.services.ProductsService
import br.symbiosys.solucoes.cronospharma.cronospharma.sym.model.SymParametros
import br.symbiosys.solucoes.cronospharma.cronospharma.sym.service.SymEventosService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/testes")
class TestesController {

    @Autowired
    private lateinit var pedidoPalmRepository: PedidoPalmRepository

    @Autowired
    private lateinit var finalizaMovimento: FinalizaMovimento

    @Autowired
    private lateinit var bloqueioMovimentoRepository: BloqueioMovimentoRepository

    @Autowired
    private lateinit var apiPetronasAuth: ApiPetronasAuth

    @Autowired
    private lateinit var symEventosService: SymEventosService

    @Autowired
    private lateinit var accountsService: AccountsService

    @Autowired
    private lateinit var productsService: ProductsService

    @Autowired
    private lateinit var orderService: OrderService

    @Value("\${app.filial.cnpj}")
    private lateinit var cnpj: String




    @PostMapping
    fun testMedquimica(@RequestBody caminho: String): Any {
        var pedido = TipoIntegracao.toCloseUP(caminho).toPedidoPalm()
        var pedidopalm = pedidoPalmRepository.save(pedido)
        pedidoPalmRepository.toMovimento(pedidopalm)
        pedidopalm.SituacaoPedido = "C"
        finalizaMovimento.finaliza(pedidopalm)
        bloqueioMovimentoRepository.executaRegrasTipoGravarRetornos(pedidopalm)
//        val pedido_atualizado = pedidoPalmRepository.findById(pedidopalm.IdPedidoPalm!!)
//        return Pedido().gerarRetorno(cnpj, pedido_atualizado!!, Diretorio())

        return "OK"
    }

    @GetMapping
    fun test(): Any {
       val response = apiPetronasAuth.getAccessToken()
        return response.body!!
    }

    @GetMapping("/movimento")
    fun analisarEventos(): Any {
       return symEventosService.analisarEventos()
    }

    @GetMapping("/account")
    fun analisaraccount(): Any {
        return accountsService.sendAccountsToSfa(SymParametros().apply {
            codigoFilial = "01"
            codigoDistribuidorPetronas = "FAALRN"
        })
    }

    @GetMapping("/product")
    fun sendProduct(): Any {

        productsService.sendProductsToSFA(SymParametros().apply {
            codigoFilial = "01"
            codigoDistribuidorPetronas = "FAALRN"
        })

        return "OK"
    }
    @GetMapping("/productkey")
    fun sendProductKey(): Any {

        productsService.sendKeyProductsToSFA(SymParametros().apply {
            codigoFilial = "01"
            codigoDistribuidorPetronas = "FAALRN"
        })

        return "OK"
    }
    @GetMapping("/inventory")
    fun sendEstoque(): Any {

        return productsService.sendKeyProductsInventoryToSFA(SymParametros().apply {
            codigoFilial = "01"
            codigoLocal = "01"
            codigoDistribuidorPetronas = "FAALRN"
        })
    }

    @PostMapping("/convert")
    fun convertOrderToMovimento() {
        return orderService.convertOrderToMovimento()
    }
}