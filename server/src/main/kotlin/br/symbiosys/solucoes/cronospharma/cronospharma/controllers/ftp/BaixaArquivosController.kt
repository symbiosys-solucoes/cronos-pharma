package br.symbiosys.solucoes.cronospharma.cronospharma.controllers.ftp

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.diretorios.DiretoriosRepository
import br.symbiosys.solucoes.cronospharma.cronospharma.ftp.ClienteFTP
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/integradores")
class BaixaArquivosController
    (
    private val diretoriosRepository: DiretoriosRepository
            )
{
    @PostMapping("/{id}/download", produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun lista(@PathVariable id: Long, @RequestParam(name = "arquivo") arquivo: String): ResponseEntity<Any>{
        val diretorio = diretoriosRepository.findById(id)
        if(diretorio ==null){
            return ResponseEntity.notFound().build()
        }

        val clientFtp = ClienteFTP(diretorio)
        clientFtp.abreConexaoFTP()
        clientFtp.downloadArquivo(diretorio.diretorioPedidoFTP + arquivo, diretorio.diretorioPedidoLocal!!+ arquivo)
        clientFtp.fechaConexaoFTP()


        val response = "{\"resultado\": \"O arquivo ${arquivo}, foi baixado na pasta ${diretorio.diretorioPedidoLocal}\" \n}"

        return ResponseEntity.ok(response)

    }
}