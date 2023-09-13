package br.symbiosys.solucoes.cronospharma.processamento.services

import br.symbiosys.solucoes.cronospharma.entidades.TipoIntegracao
import br.symbiosys.solucoes.cronospharma.entidades.consys.ItemCONSYS
import br.symbiosys.solucoes.cronospharma.entidades.consys.PedidoCONSYS
import br.symbiosys.solucoes.cronospharma.entidades.cronos.PedidoPalm
import java.io.BufferedReader
import java.io.IOException
import java.io.StringReader
import java.time.LocalDate


class TextoParaPedidoPalmHelper {


    fun converter(conteudo: String, integracao: TipoIntegracao): List<PedidoPalm> {
        when (integracao) {
            TipoIntegracao.CONSYS -> {
                return toConsys(conteudo)
            }
        }
        return emptyList()
    }

    fun toConsys(conteudo: String): List<PedidoPalm> {
        val separador = Regex("\n(?=1)")
        val pedidosCONSYS: MutableList<PedidoCONSYS> = mutableListOf()

        try{
            // Colocando conteudo do arquivo e colocando em uma unica String
            var conteudoArquivo: String? = null
            val inputString = StringReader(conteudo)
            val bufferedReader = BufferedReader(inputString)

            bufferedReader.forEachLine {
                conteudoArquivo += it + "\n"
            }

            inputString.close()
            bufferedReader.close()

            if (conteudoArquivo == null) {
                TipoIntegracao.logger.warn("Nao foi possivel dividir arquivo")
                throw IOException("Erro ao ler arquivo")
            }
            val arquivoSeparado: MutableList<String> = conteudoArquivo.split(separador).toMutableList()



            arquivoSeparado.forEach {
                val linhas: List<String> = it.split("\n")
                var codigoCliente:String? = null
                var numeroPedidoCliente:String? = null
                var cnpjCliente: String? = null
                var dataPedido: LocalDate? = null
                var itens: MutableList<ItemCONSYS> = mutableListOf()


                linhas.forEach {
                    if(it.length > 2) {
                        when (it.substring(0, 1)) {
                            "n" -> codigoCliente = it.substring(5, 13).trim()
                            "2" -> numeroPedidoCliente = it.substring(1, 11).trim()
                            "3" -> cnpjCliente = it.substring(1, 15).trim()
                            "5" -> dataPedido = LocalDate.of(
                                it.substring(1, 5).trim().toInt(),
                                it.substring(5, 7).trim().toInt(),
                                it.substring(7, 9).trim().toInt()
                            )
                            "7" -> {
                                itens.add(
                                    ItemCONSYS(
                                        codigo = it.substring(1, 11).trim(),
                                        quantidade = it.substring(11, 16).trim().toDouble(),
                                        codigoCONSYS = it.substring(16, 27).trim(),
                                    )
                                )
                            }

                        }

                    }
                }

                pedidosCONSYS.add(
                    PedidoCONSYS(
                        cliente = codigoCliente,
                        idPedido = numeroPedidoCliente,
                        cnpj = cnpjCliente,
                        dataPedido = dataPedido,
                        produtos = itens
                    )
                )

            }


        } catch(e: Exception) {
            e.printStackTrace()
        }
        return pedidosCONSYS.map { pedidoCONSYS -> pedidoCONSYS.toPedidoPalm() }
    }

}