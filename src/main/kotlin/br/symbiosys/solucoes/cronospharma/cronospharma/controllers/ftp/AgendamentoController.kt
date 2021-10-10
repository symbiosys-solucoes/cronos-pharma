package br.symbiosys.solucoes.cronospharma.cronospharma.controllers.ftp

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.diretorios.DiretoriosRepository
import br.symbiosys.solucoes.cronospharma.cronospharma.ftp.ClienteFTP
import br.symbiosys.solucoes.cronospharma.cronospharma.processamento.Agendamento
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/processamento")
class AgendamentoController
{
    @Autowired
    lateinit var agendamento: Agendamento
    @PostMapping
    public fun executa(): ResponseEntity<Any>{

        agendamento.executeWithoutSchedule()
        return ResponseEntity.ok().build()

    }
}