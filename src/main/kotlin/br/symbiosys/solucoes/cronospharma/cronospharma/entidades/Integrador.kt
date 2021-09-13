package br.symbiosys.solucoes.cronospharma.cronospharma.entidades

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos.PedidoPalm
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.diretorios.Diretorio
import br.symbiosys.solucoes.cronospharma.cronospharma.processamento.Arquivo
import java.io.File

interface Integrador {

    fun toPedidoPalm(): PedidoPalm

    fun gerarRetorno(cnpj:String, pedidoPalm: PedidoPalm, diretorio: Diretorio): File
}