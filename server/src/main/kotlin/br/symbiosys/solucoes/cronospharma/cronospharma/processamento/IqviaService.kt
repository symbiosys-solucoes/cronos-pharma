package br.symbiosys.solucoes.cronospharma.cronospharma.processamento

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.TipoIntegracao
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos.*
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.diretorios.Diretorio
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.diretorios.DiretoriosRepository
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.ems.PedidoEMS
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.iqvia.PedidoIqvia
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class IqviaService (private val pedidoPalmRepository: PedidoPalmRepository,
                    private val finalizaMovimento: FinalizaMovimento,
                    private val bloqueioMovimentoRepository: BloqueioMovimentoRepository,
                    private val diretoriosRepository: DiretoriosRepository,
                    private val retornoNotaIqviaRepository: RetornoNotaIqviaRepository,
) {
    private val logger = LoggerFactory.getLogger(IqviaService::class.java)
    @Value("\${app.filial.cnpj}")
    lateinit var cnpj: String
    private fun findPedidoPalm(id: Long) : PedidoPalm {
        val pedidoPalm = pedidoPalmRepository.findById(id)
        if (pedidoPalm == null) {
            logger.error("Pedido com id: $id nao foi encontrado na base de dados")
            throw PedidoNotFoundException("Pedido com id: $id nao foi encontrado na base de dados")
        }
        return  pedidoPalm
    }
    fun inserePreVenda(id: Long): PedidoPalm? {
        val pedido = findPedidoPalm(id)

        if (!pedido.CodCliFor.isNullOrBlank()) {
            logger.info("Gerando Pre-Venda do pedido numero: ${pedido.NumPedidoPalm}")
            var result = pedidoPalmRepository.toMovimento(pedido) ?: ""
            if (result == ""){
                logger.error("erro ao gerar Pre-Venda do pedido de numero: ${pedido.NumPedidoPalm}")
            }else{
                pedido.SituacaoPedido = "C"
            }

            //var resultFim =
            finalizaMovimento.finaliza(pedido)
            logger.info("O pedido de numero: ${pedido.NumPedidoPalm}, Gerou a Pre-Venda numero: ${pedido.NumPedidoCRONOS}")

            return findPedidoPalm(pedido.IdPedidoPalm!!)

        } else {
            logger.info("Cliente nao cadastrado na base, sistema nao ira gerar pre-venda")
            return null
        }
    }

    fun atualizaStatusRetorno(id: Long): PedidoPalm? {
        val pedido = findPedidoPalm(id)
        bloqueioMovimentoRepository.executaRegrasTipoGravarRetornos(pedido)
        val convertido = findPedidoPalm(id)
        logger.info("O pedido numero: ${convertido.NumPedidoPalm}, gerou o status: ${convertido.DscRetorno}")

        return convertido
    }


    fun gerarArquivoDeRetornoPedido(id: Long) {
        if (LocalDate.now() <= LocalDate.of(2023, 1, 31)){
            val pedidoPalm = findPedidoPalm(id)
            val diretorio = diretoriosRepository.findAll().filter { dir -> dir.tipoIntegracao.equals(TipoIntegracao.REDEFTB)}.firstOrNull()
            if (diretorio == null) {
                logger.error("Nao foi encontrado configuracao para a integracao ${TipoIntegracao.REDEFTB.name}")
                throw DiretorioNotFoundException("Nao foi encontrado configuracao para a integracao ${TipoIntegracao.REDEFTB.name}")
            }

            val retorno = PedidoIqvia().gerarRetorno(cnpj, pedidoPalm, diretorio)
            val arquivo = Arquivo()
            arquivo.criaArquivo(retorno)
            pedidoPalmRepository.updateNomeArquivoRetorno(retorno.name, pedidoPalm.IdPedidoPalm!!)
            logger.info("Gerado o arquivo de retorno do pedido numero: ${pedidoPalm.NumPedidoPalm}")
        }
    }

    fun gerarArquivoRetornoNF(id: Long) {

        if (LocalDate.now() <= LocalDate.of(2023, 1, 31)){
            val pedidoPalm = findPedidoPalm(id)
            val diretorio = diretoriosRepository.findAll().filter { dir -> dir.tipoIntegracao.equals(TipoIntegracao.REDEFTB)}.firstOrNull()
            if (diretorio == null) {
                logger.error("Nao foi encontrado configuracao para a integracao ${TipoIntegracao.REDEFTB.name}")
                throw DiretorioNotFoundException("Nao foi encontrado configuracao para a integracao ${TipoIntegracao.REDEFTB.name}")
            }

            val retornos = retornoNotaIqviaRepository.findRetornos(listOf(pedidoPalm.IdPedidoPalm!!), force = true)
            val arquivo = Arquivo()
            if(!retornos.isNullOrEmpty()) {
                retornos.forEach { ret ->
                    val file = ret.gerarRetorno(cnpj, diretorio)
                    arquivo.criaArquivo(file)
                    pedidoPalmRepository.updateNomeArquivoRetornoNF(file.name, pedidoPalm.IdPedidoPalm)
                    logger.info("Gerado o arquivo de retorno NF do pedido numero: ${pedidoPalm.NumPedidoPalm}")
                }
            }


        }

    }



}