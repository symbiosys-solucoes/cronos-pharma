package br.symbiosys.solucoes.cronospharma.cronospharma.sym.service

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.request.Order
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.petronas.model.request.OrderRequest
import br.symbiosys.solucoes.cronospharma.cronospharma.sym.gateway.repository.SymEventosRepository
import br.symbiosys.solucoes.cronospharma.cronospharma.sym.model.SymEventos
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class SymEventosService {

    @Autowired
    lateinit var symEventosRepository: SymEventosRepository

    @Autowired
    lateinit var clientesEventosService: ClientesEventosService
    @Autowired
    lateinit var cprEventosService: CprEventosService
    @Autowired
    lateinit var movimentosEventosService: MovimentosEventosService
    @Autowired
    lateinit var produtosEventosService: ProdutosEventosService

    fun analisarEventos(): MutableList<OrderRequest> {
       // val eventos = symEventosRepository.findAll(Sort.by(Sort.Direction.ASC, "data_evento"))
        val eventos = symEventosRepository.findAll()
        var clientesEventos = mutableListOf<SymEventos>()
        var produtosEventos = mutableListOf<SymEventos>()
        var movimentosEventos = mutableListOf<SymEventos>()
        var cprEventos = mutableListOf<SymEventos>()
        for (evento in eventos) {
            if (evento.tabela == "Cli_For") {
                clientesEventos.add(evento)
            }
            if (evento.tabela == "Produtos") {
                produtosEventos.add(evento)
            }
            if (evento.tabela == "Movimento") {
                movimentosEventos.add(evento)
            }
            if (evento.tabela == "CPR") {
                cprEventos.add(evento)
            }
        }

//        clientesEventosService.processarEventos(clientesEventos)
//        produtosEventosService.processarEventos(produtosEventos)
       return movimentosEventosService.processarEventos(movimentosEventos)
//        cprEventosService.processarEventos(cprEventos)

    }

}

