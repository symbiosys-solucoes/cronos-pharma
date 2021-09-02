package br.symbiosys.solucoes.cronospharma.cronospharma.entidades

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.ems.EMS
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.ems.ItemEMS
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.time.LocalDate

enum class TipoIntegracao {


    CONSYS,
    EMS;

    companion object {



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


                    if (it != null) {
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
