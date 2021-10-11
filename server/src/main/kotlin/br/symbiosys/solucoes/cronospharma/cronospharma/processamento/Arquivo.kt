package br.symbiosys.solucoes.cronospharma.cronospharma.processamento

import org.slf4j.LoggerFactory
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException

class Arquivo {
    val logger = LoggerFactory.getLogger(Arquivo::class.java)
    fun removeArquivo(caminho: String){

        val arq = File(caminho)
        if(!arq.exists()){
            logger.info("Arquivo, $caminho não existe")
            return
        }

        if(!arq.delete()){
            logger.warn("não foi possivel excluir o arquivo")
        }


    }
    fun criaDiretorio(caminho: String){
        val diretorio = File(caminho)
        if(diretorio.exists()){
            return
        }

        diretorio.mkdir()

    }

    fun criaArquivo(conteudo:String, caminho: String){

        val file = File(caminho)

        if (file.exists()){
            return
        }

        try {
            BufferedWriter(FileWriter(caminho)).append(conteudo).close()
        } catch (e: IOException){
            e.printStackTrace()
        }
    }

    fun criaArquivo(file:File){

        if (file.exists()){
            return
        }

        try {
            val arquivo = FileWriter(file)
            val output = BufferedWriter(arquivo)
            output.flush()
            output.close()

        } catch (e: IOException){
            e.printStackTrace()
        }
    }

    fun listaArquivos(diretorio: String): List<String> {
        val file = File(diretorio)
        if(!file.exists() && !file.isDirectory){
            throw IOException("Caminho não existe ou não é uma pasta")
        }
        return file.listFiles().map { file -> file.absolutePath }
    }

    fun moverArquivo(origem: String, destino: String){
        val arquivoOrigem= File(origem)
        val arquivoDestino = File(destino)
        if (arquivoDestino.exists()){
            arquivoDestino.delete()
        }
        if (arquivoOrigem.exists()){
            arquivoOrigem.renameTo(arquivoDestino)
        }
    }
}