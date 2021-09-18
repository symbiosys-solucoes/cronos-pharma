package br.symbiosys.solucoes.cronospharma.cronospharma.processamento

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.TipoIntegracao
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos.BloqueioMovimentoRepository
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos.FinalizaMovimento
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos.PedidoPalm
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos.PedidoPalmRepository
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.diretorios.Diretorio
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.diretorios.DiretoriosRepository
import br.symbiosys.solucoes.cronospharma.cronospharma.ftp.ClienteFTP
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled

import org.springframework.stereotype.Component
import java.sql.SQLException
import kotlin.math.log

@Component
@EnableScheduling
class Agendamento (
    val diretoriosRepository: DiretoriosRepository,
    val pedidoPalmRepository: PedidoPalmRepository,
    val finalizaMovimento: FinalizaMovimento,
    val bloqueioMovimentoRepository: BloqueioMovimentoRepository
        ) {
    val logger = LoggerFactory.getLogger(Agendamento::class.java)
    // Passo 1 - Pegar os Diretórios
    val diretorios: List<Diretorio> = diretoriosRepository.findAll()
    val arquivo: Arquivo = Arquivo()

    // Passo 2 - Iterar sobre todos os Diretórios a cada x tempo definido no application.properties

    // Passo 3 - Fazer download dos devidos arquivos
        fun baixarArquivos(diretorios: List<Diretorio>){

            diretorios.forEach { dir ->
                logger.info("Procurando arquivos ${dir.tipoIntegracao.name}")
                val clienteFTP = ClienteFTP( server = dir.url, user = dir.login, password = dir.senha)
                clienteFTP.abreConexaoFTP()
                val arquivos = clienteFTP.listaArquivos(dir.diretorioPedidoFTP ?: throw SQLException("Caminho FTP não pode estar nulo ou em branco"))
                if(arquivos.isEmpty()){
                    logger.info("Não Existe arquivos para baixar!")
                    return
                }
                arquivos.forEach { arq ->
                    clienteFTP.downloadArquivo(arq, dir.diretorioPedidoLocal + arq ?: throw SQLException("Caminho FTP não pode estar nulo ou em branco"))
                }
                clienteFTP.fechaConexaoFTP()
            }
        }
    // Passo 4 - Inserir Pedido no Cronos
    fun inserePedidos(diretorios: List<Diretorio>): List<PedidoPalm> {
        val pedidos = geraPedidos(diretorios)
        var persistedPedidos = mutableListOf<PedidoPalm>()

        pedidos.forEach { pedido ->
            persistedPedidos.add(pedidoPalmRepository.save(pedido))
        }

        return persistedPedidos
    }

    fun geraPedidos(diretorios: List<Diretorio>): List<PedidoPalm> {
        var pedidos = mutableListOf<PedidoPalm>()
        diretorios.forEach { dir ->
            if(dir == null){
                throw SQLException("Caminho nao existe localmente")
            }
            val arquivos = arquivo.listaArquivos(dir.diretorioPedidoLocal!!)
            if( arquivos.isEmpty()){
                logger.info("nao existe arquivos para ser importado de ${dir.tipoIntegracao.name}")
                return pedidos
            }
            arquivos.forEach { arq ->
                when(dir.tipoIntegracao){
                    TipoIntegracao.EMS -> {
                      pedidos.add(TipoIntegracao.toEms(arq).toPedidoPalm())
                    }
                    TipoIntegracao.CONSYS -> {
                      TipoIntegracao.toConsys(arq).forEach { pedidos.add(it.toPedidoPalm()) }
                    }
                }
            this.arquivo.moverArquivo(arq, dir.diretorioImportadosLocal + arq.replace(dir.diretorioPedidoLocal,""))
            }
        }
        return pedidos
    }

    // Passo 5 - Copiar pedido para a pasta de pedidos importados

    // Passo 6 - Transformar Pedido em Pré-Venda
    fun inserePreVenda(pedidos: List<PedidoPalm>): List<PedidoPalm> {
        var convertidos = mutableListOf<PedidoPalm>()

        pedidos.forEach {
           var result = pedidoPalmRepository.toMovimento(it) ?: ""
            if (result == ""){
                logger.info("erro ao gerar Pre-Venda do pedido de id: ${it.IdPedidoPalm}")
            }
            var resultFim = finalizaMovimento.finaliza(it)
            logger.info("O pedido id: ${it.IdPedidoPalm} gerou o movimento com status = $resultFim")
            convertidos.add(pedidoPalmRepository.findById(it.IdPedidoPalm!!)!!)

        }
        return convertidos
    }
    // Passo 7 - Atualizar o Status de Retorno
    fun atualizaStatusRetorno(pedidos: List<PedidoPalm>){
        var convertidos = mutableListOf<PedidoPalm>()
        pedidos.forEach { bloqueioMovimentoRepository.executaRegrasTipoGravarRetornos(it)
            convertidos.add(pedidoPalmRepository.findById(it.IdPedidoPalm!!)!!)
        }
    }
    // Passo 8 - Gerar arquivo de Retorno

    // Passo 9 - Fazer upload de arquivo de Retorno

    // Passo 10 - Excluir Arquivo de Retorno

}

