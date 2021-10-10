package br.symbiosys.solucoes.cronospharma.cronospharma.controllers.ftp

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.diretorios.DiretoriosRepository
import br.symbiosys.solucoes.cronospharma.cronospharma.ftp.ClienteFTP
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/integradores")
class ListaArquivosController
    (
    private val diretoriosRepository: DiretoriosRepository
            )
{
    @GetMapping("/{id}/arquivos")
    public fun lista(@PathVariable id: Long): ResponseEntity<Any>{
        val diretorio = diretoriosRepository.findById(id)
        if(diretorio ==null){
            return ResponseEntity.notFound().build()
        }

        val clientFtp = ClienteFTP(diretorio)
        clientFtp.abreConexaoFTP()
        println(diretorio)
        val listaArquivos = clientFtp.listaArquivos(diretorio.diretorioPedidoFTP!!)
        clientFtp.fechaConexaoFTP()

        return ResponseEntity.ok(listaArquivos)

    }
}