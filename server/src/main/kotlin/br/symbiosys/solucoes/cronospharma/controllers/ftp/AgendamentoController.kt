package br.symbiosys.solucoes.cronospharma.controllers.ftp

import br.symbiosys.solucoes.cronospharma.processamento.Agendamento
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/processamento")
class AgendamentoController
{
    @Autowired
    lateinit var agendamento: Agendamento
    @PostMapping
    fun executa(): ResponseEntity<Any>{
        agendamento.execute()
        //agendamento.executeWithoutSchedule()
        return ResponseEntity.ok().build()

    }

}