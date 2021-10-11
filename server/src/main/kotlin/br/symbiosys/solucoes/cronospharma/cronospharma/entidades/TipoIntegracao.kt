package br.symbiosys.solucoes.cronospharma.cronospharma.entidades

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.consys.ItemCONSYS
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.consys.PedidoCONSYS
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.ems.EMS
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.ems.ItemEMS
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.time.LocalDate

enum class TipoIntegracao {


    CONSYS,
    EMS;

    companion object {
        val logger = LoggerFactory.getLogger(TipoIntegracao::class.java)
        fun toConsys(origem: String): List<PedidoCONSYS>{

            val arquivo = File(origem)
            val separador = Regex("\n(?=1)")
            val pedidosCONSYS: MutableList<PedidoCONSYS> = mutableListOf()

            try{
                // Colocando conteudo do arquivo e colocando em uma unica String
                val fileReader = FileReader(arquivo)
                var conteudoArquivo: String? = null
                val bufferedReader = BufferedReader(fileReader)

                bufferedReader.forEachLine {
                    conteudoArquivo += it + "\n"
                }

                fileReader.close()
                bufferedReader.close()

                if (conteudoArquivo == null) {
                    logger.warn("Nao foi possivel dividir arquivo")
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
            return pedidosCONSYS
        }

        fun toEms(origem: String): br.symbiosys.solucoes.cronospharma.cronospharma.entidades.ems.EMS {
            var cnpj:String? = null
            var numero: String? = null
            var dataPedido: LocalDate? = null
            var numeroPedidoCliente: String? = null
            var codigoRepresentante: String? = null
            var itens: MutableList<ItemEMS> = mutableListOf()
            val arquivo = File(origem)

            try {
                val fileReader = FileReader(arquivo)

                val bufferedReader = BufferedReader(fileReader)

                bufferedReader.forEachLine {



                    when(it.substring(0,1)) {
                        "1" -> {
                            cnpj = it.substring(2,16)
                            numero = it.substring(16,28).trim()
                            dataPedido = LocalDate.of(it.substring(32,36).toInt(), it.substring(30,32).toInt(), it.substring(28,30).toInt())
                            numeroPedidoCliente = it.substring(43,58).trim()
                            codigoRepresentante = it.substring(58,62)
                        }
                        "2" -> {
                            itens.add(
                                ItemEMS(
                                    codigoProduto = it.substring(13,26),
                                    quantidade = it.substring(26,31).toDouble(),
                                    desconto = "${it.substring(31,34)}.${it.substring(34,36)}".toDouble(),
                                    prazo = it.substring(36,39)
                                )
                            )

                        }
                    }



                }

                fileReader.close()
                bufferedReader.close()

            } catch (e: Exception){
                e.printStackTrace()
            }
            return EMS(
                codigoCliente = cnpj,
                numeroPedido = numero,
                dataPedido = dataPedido,
                numeroPedidoCliente = numeroPedidoCliente,
                produtos = itens,
                codigoRepresentante = codigoRepresentante

            )
        }
    }


}
