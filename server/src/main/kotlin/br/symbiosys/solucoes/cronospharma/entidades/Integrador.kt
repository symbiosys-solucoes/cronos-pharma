package br.symbiosys.solucoes.cronospharma.entidades

import br.symbiosys.solucoes.cronospharma.entidades.cronos.PedidoPalm
import br.symbiosys.solucoes.cronospharma.entidades.diretorios.Diretorio
import java.io.File

interface Integrador  {

    fun toPedidoPalm(): PedidoPalm

    fun gerarRetorno(cnpj:String, pedidoPalm: PedidoPalm, diretorio: Diretorio): File


}