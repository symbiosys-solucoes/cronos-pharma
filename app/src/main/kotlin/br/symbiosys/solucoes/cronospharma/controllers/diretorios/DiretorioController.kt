package br.symbiosys.solucoes.cronospharma.controllers.diretorios

import br.symbiosys.solucoes.cronospharma.entidades.diretorios.Diretorio
import br.symbiosys.solucoes.cronospharma.entidades.diretorios.DiretoriosRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/integradores")
class DiretorioController (
    private val repository: DiretoriosRepository
        ){

    @GetMapping
    fun listaTodos(): ResponseEntity<List<Diretorio>> {

        return ResponseEntity.ok(repository.findAll())
    }

}