package br.symbiosys.solucoes.cronospharma.processamento.services.controller

import br.symbiosys.solucoes.cronospharma.processamento.services.ArquivoService
import br.symbiosys.solucoes.cronospharma.processamento.services.DiretorioService
import br.symbiosys.solucoes.cronospharma.processamento.services.DownloadUploadService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component


@Component
class AgendamentoService
    (private val diretorioService: DiretorioService,
            private val downloadUploadService: DownloadUploadService,
            private val arquivoService: ArquivoService)
{


    @Value("\${app.filial.cnpj}")
    lateinit var cnpj: String

    fun start() {
        val operadores = diretorioService.findAll()
        operadores.forEach { operador ->
            downloadUploadService.baixarConteudoDeUmaPasta(operador)
            val textos = arquivoService.converterArquivosParaTexto(operador)
        }



    }






}