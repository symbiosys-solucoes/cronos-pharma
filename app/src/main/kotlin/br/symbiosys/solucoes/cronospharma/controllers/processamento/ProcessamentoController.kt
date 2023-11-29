package br.symbiosys.solucoes.cronospharma.controllers.processamento

import br.symbiosys.solucoes.cronospharma.processamento.Agendamento
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ProcessamentoController {

    @GetMapping("/api/v1/status")
    fun execucoes(): ResponseEntity<Any>{

        return ResponseEntity.ok(Agendamento.processamento)
    }
}