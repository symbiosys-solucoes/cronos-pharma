package br.symbiosys.solucoes.cronospharma.controllers.iqvia

import br.symbiosys.solucoes.cronospharma.processamento.IqviaService
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/iqvia")
class IqviaController (private val service: IqviaService){

    @PostMapping("/prevenda/{id}")
    fun generatePreVenda(@PathVariable id: Long) {
        service.inserePreVenda(id)
    }

    @PostMapping("/processar/pedido/{id}")
    fun processPedido(@PathVariable id: Long) {
        service.atualizaStatusRetorno(id)
    }

    @PostMapping("/gerar/pedido/{id}")
    fun generateReturnPedido(@PathVariable id: Long) {
        service.gerarArquivoDeRetornoPedido(id)
    }

    @PostMapping("/gerar/nf/{id}")
    fun generateReturnNF(@PathVariable id: Long) {
        service.gerarArquivoRetornoNF(id)
    }
}