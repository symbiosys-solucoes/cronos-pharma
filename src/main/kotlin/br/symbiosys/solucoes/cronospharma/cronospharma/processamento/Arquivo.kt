package br.symbiosys.solucoes.cronospharma.cronospharma.processamento

import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException

class Arquivo {


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
            BufferedWriter(FileWriter(file))
        } catch (e: IOException){
            e.printStackTrace()
        }
    }
}