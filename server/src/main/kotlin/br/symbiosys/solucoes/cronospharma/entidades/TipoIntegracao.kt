package br.symbiosys.solucoes.cronospharma.entidades

import br.symbiosys.solucoes.cronospharma.entidades.consys.ItemCONSYS
import br.symbiosys.solucoes.cronospharma.entidades.consys.PedidoCONSYS
import br.symbiosys.solucoes.cronospharma.entidades.ems.PedidoEMS
import br.symbiosys.solucoes.cronospharma.entidades.ems.ItemEMS
import br.symbiosys.solucoes.cronospharma.entidades.iqvia.ItemPedidoIqvia
import br.symbiosys.solucoes.cronospharma.entidades.iqvia.PedidoIqvia
import br.symbiosys.solucoes.cronospharma.entidades.closeup.ItemPedido
import br.symbiosys.solucoes.cronospharma.entidades.closeup.Pedido
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.lang.NumberFormatException
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

enum class TipoIntegracao {


    CONSYS,
    EMS,
    REDEFTB,
    CLOSEUP;

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

        fun toEms(origem: String): PedidoEMS {
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
            return PedidoEMS(
                codigoCliente = cnpj,
                numeroPedido = numero,
                dataPedido = dataPedido,
                numeroPedidoCliente = numeroPedidoCliente,
                produtos = itens,
                codigoRepresentante = codigoRepresentante

            )
        }

        fun toRedeFTB(origem: String): PedidoIqvia {
            // Cabecalho
            var cnpjCliente:String? = null
            var tipoFaturamento: Int? = null
            var apontadorPromocao: String? = null
            var descontoDistribuidor: String? = null

            // Dados do pedido 1
            var codigoProjeto: String? = null
            var numeroPedido: String? = null
            var cnpjCD: String? = null

            // Faturamento
            var tipoPagamento: String? = null
            var codigoDeterminado: String? = null
            var numeroDias: String? = null
            var numeroPedidoPrincipal: String? = null

            // Dados Pedido 2
            var numeroPedidoCliente: String? = null

            // Data/Hora Pedido
            var dataPedido: LocalDate? = null
            var horaPedido: LocalTime? = null

            // Itens do Pedido
            var itens: MutableList<ItemPedidoIqvia> = mutableListOf()

            // Rodape
            var quantidadeItems: Int? = null

            val arquivo = File(origem)
            try {
                val fileReader = FileReader(arquivo)

                val bufferedReader = BufferedReader(fileReader)


                bufferedReader.forEachLine {
                    if (it.isNullOrEmpty()) {
                        return@forEachLine
                    }
                    when(it.substring(0,1)) {
                        "1" -> {
                            cnpjCliente = it.substring(1,15).trim()
                            tipoFaturamento = it.substring(15, 16).trim().toInt()
                            apontadorPromocao = it.substring(16, 29).trim()
                            descontoDistribuidor = it.substring(29, 31).trim()
                        }
                        "2" -> {
                            codigoProjeto = it.substring(1, 2).trim()
                            numeroPedido = it.substring(2, 17).trim()
                            cnpjCD = it.substring(17,31).trim()
                        }
                        "3" -> {
                            tipoPagamento = it.substring(1,2).trim()
                            codigoDeterminado = it.substring(2,6).trim()
                            numeroDias = it.substring(6, 9).trim()
                            numeroPedidoPrincipal = it.substring(9,24).trim()
                        }
                        "4" -> {
                            numeroPedidoCliente = it.substring(1,16).trim()
                        }
                        "5" -> {
                            dataPedido = LocalDate.of(it.substring(1,5).trim().toInt(), it.substring(5,7).trim().toInt(), it.substring(7,9).trim().toInt())
                        }
                        "6" -> {
                            horaPedido = LocalTime.of(it.substring(1,3).trim().toInt(), it.substring(3, 5).trim().toInt(), it.substring(5,7).trim().toInt())
                        }
                        "7" -> {
                            itens.add(
                                ItemPedidoIqvia(
                                    codigoEAN = it.substring(1,14).trim(),
                                    quantidade = it.substring(14,22).trim().toDouble(),
                                    tipoOcorrencia = it.substring(22,24). trim(),
                                    campoControleIqvia = it.substring(24,31).trim(),
                                    descontoItem = (it.substring(31,33).trim() + "." +it.substring(33,35).trim()).toDouble()
                                )
                            )
                        }
                        "8" -> {
                            quantidadeItems = it.substring(1,3).trim().toInt()
                        }
                    }
                }

                fileReader.close()
                bufferedReader.close()

            } catch (e: Exception){
                e.printStackTrace()
            }

            return PedidoIqvia(
                cnpjCliente = cnpjCliente,
                tipoFaturamento = tipoFaturamento,
                apontadorPromocao = apontadorPromocao,
                descontoDistribuidorOuVan = descontoDistribuidor,
                codigoProjeto = codigoProjeto,
                numeroPedidoIqvia = numeroPedido,
                cnpjCD = cnpjCD,
                tipoPagamento = tipoPagamento,
                codigoPrazoDeterminado = codigoDeterminado,
                numeroDiasPrazoDeterminado = numeroDias,
                numeroPedidoPrincipal = numeroPedidoPrincipal,
                numeroPedidoCliente = numeroPedidoCliente,
                dataPedido = LocalDateTime.of(dataPedido, horaPedido),
                itensPedidoIqvia = itens,
                quatidadeItems = quantidadeItems
            )
        }

        fun toCloseUP(origem: String): Pedido {
            val pedido = Pedido()
            val arquivo = File(origem)
            try {
                val fileReader = FileReader(arquivo)

                val bufferedReader = BufferedReader(fileReader)


                bufferedReader.forEachLine {

                    if (it.isNullOrEmpty()) {
                        return@forEachLine
                    }
                    when(it.substring(0,1)) {
                        "1" -> {
                            pedido.cnpjFarmacia = it.substring(1,15).trim()
                            pedido.numeroPedidoMedquimica = it.substring(15, 35).trim()
                            pedido.dataPedido = LocalDate.of(it.substring(40,44).trim().toInt(), it.substring(38,40).trim().toInt(), it.substring(36,38).trim().toInt())
                            pedido.linhaProduto = it.substring(49,50).trim()
                            pedido.cnpjDistribuidor = it.substring(51, 66).trim()
                            pedido.numeroOS = it.substring(66, 86).trim()
                            pedido.observacao = it.substring(86, 186).trim()
                            pedido.codigoRepresentante = it.substring(186,196).trim()
                            try {
                                pedido.codigoCampanha = it.substring(196, 202).trim()
                                pedido.faturamentoIntegral = it.substring(202,203).trim()
                                pedido.prazo = it.substring(203, 206).trim()
                                pedido.condicaoPrazo = it.substring(206,207).trim()
                                pedido.codigoPrazo = it.substring(207,247).trim()
                            } catch (e: StringIndexOutOfBoundsException){
                                logger.warn("Nao foi possivel carregar o codigo da campanha, faturamento integral e prazo")
                            }
                        }
                        "2" -> {
                           val item = ItemPedido()
                            item.numeroPedidoFornecedor = pedido.numeroPedidoMedquimica
                            item.codigoBarras = it.substring(21,34).trim()
                            item.quantidade = it.substring(34,39).trim().toInt()
                            item.condicacaoComercial = it.substring(39,40).trim()
                            item.desconto = "${it.substring(43,46).trim()}.${it.substring(46,48).trim()}"
                            item.prazo = it.substring(48,51).trim()
                            item.condicaoDesconto = it.substring(51,52).trim()
                            item.condicaoPrazo = it.substring(52,53).trim()
                            item.codigoPrazo = it.substring(53, 93).trim()
                            try {
                            item.codigoOferta = it.substring(93, 113).trim()

                            item.preco = BigDecimal("${it.substring(113,118).trim()}.${it.substring(118,120).trim()}")
                            } catch (e: NumberFormatException){
                                logger.warn("Nao foi informado preco para o produto")
                            }
                            pedido.items.add(item)
                        }
                    }
                }

                fileReader.close()
                bufferedReader.close()

            } catch (e: Exception){
                e.printStackTrace()
            }

            return pedido
        }



    }


}
