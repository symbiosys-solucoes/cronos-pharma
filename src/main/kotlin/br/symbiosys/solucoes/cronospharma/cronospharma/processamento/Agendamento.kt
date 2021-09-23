package br.symbiosys.solucoes.cronospharma.cronospharma.processamento

import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.TipoIntegracao
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos.BloqueioMovimentoRepository
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos.FinalizaMovimento
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos.PedidoPalm
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos.PedidoPalmRepository
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.diretorios.Diretorio
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.diretorios.DiretoriosRepository
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.ems.EMS
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.ems.EMSRepository
import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.ems.geraEstoqueEMS
import br.symbiosys.solucoes.cronospharma.cronospharma.ftp.ClienteFTP
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.sql.SQLException

@Component
@EnableScheduling
class Agendamento (
    val diretoriosRepository: DiretoriosRepository,
    val pedidoPalmRepository: PedidoPalmRepository,
    val finalizaMovimento: FinalizaMovimento,
    val bloqueioMovimentoRepository: BloqueioMovimentoRepository,
    val emsRepository: EMSRepository
) {
    val logger = LoggerFactory.getLogger(Agendamento::class.java)
    @Value("\${app.filial.cnpj}")
    lateinit var cnpj: String

    // To-Do:
    // Passo 1 - Pegar os Diretórios
    // Passo 2 - Iterar sobre todos os Diretórios a cada x tempo definido no application.properties
    // Passo 3 - Fazer download dos devidos arquivos
    // Passo 4 - Inserir Pedido no Cronos
    // Passo 5 - Copiar pedido para a pasta de pedidos importados
    // Passo 6 - Transformar Pedido em Pré-Venda
    // Passo 7 - Atualizar o Status de Retorno
    // Passo 8 - Gerar arquivo de Retorno
    // Passo 9 - Fazer upload de arquivo de Retorno
    // Passo 10 - Excluir Arquivo de Retorno


    val diretorios: List<Diretorio> = diretoriosRepository.findAll()
    val arquivo: Arquivo = Arquivo()

    @Scheduled(cron = "\${app.cron.busca.ftp}")
    fun execute(){
        this.diretorios.forEach { diretorio ->

            baixarArquivos(diretorio)
            val pedidos = inserePedidos(diretorio)
            val pedidosFinalizados = inserePreVenda(pedidos)
            val pedidosComStatusRetorno = atualizaStatusRetorno(pedidosFinalizados)

            gerarArquivoDeRetorno(pedidosComStatusRetorno, diretorio)

            uploadArquivos(diretorio, "RETORNO")

            logger.info("Concluido o Processo para o: ${diretorio.login}")
            //pedidosComStatusRetorno.forEach { println(it.IdPedidoPalm) }

        }
    }

    @Scheduled(cron = "\${app.cron.gera.estoque}")
    fun executeEstoque(){
        this.diretorios.forEach{
            gerarArquivoDeEstoque(it)
            uploadArquivos(it, "ESTOQUE")
        }
    }

    private fun baixarArquivos(diretorio: Diretorio){

        logger.info("Procurando arquivos ${diretorio.tipoIntegracao.name}")
        val clienteFTP = ClienteFTP( server = diretorio.url, user = diretorio.login, password = diretorio.senha)
        clienteFTP.abreConexaoFTP()
        val arquivos = clienteFTP.listaArquivos(diretorio.diretorioPedidoFTP ?: throw SQLException("Caminho FTP não pode estar nulo ou em branco"))

        if(arquivos.isEmpty()) {
            logger.info("Não Existe arquivos para baixar!")
            return
        }

        arquivos.forEach { arq ->
            logger.info(arq)
            clienteFTP.downloadArquivo(
                diretorio.diretorioPedidoFTP + arq,
                diretorio.diretorioPedidoLocal + arq,
            )
        }
        clienteFTP.fechaConexaoFTP()

    }

    private fun inserePedidos(diretorio: Diretorio): List<PedidoPalm> {
        val pedidos = geraPedidos(diretorio)
        var persistedPedidos = mutableListOf<PedidoPalm>()

        pedidos.forEach { pedido ->
            persistedPedidos.add(pedidoPalmRepository.save(pedido))
        }

        return persistedPedidos
    }

    private fun geraPedidos(diretorio: Diretorio): List<PedidoPalm> {

        var pedidos = mutableListOf<PedidoPalm>()

        if(diretorio == null){
            throw SQLException("Caminho nao existe localmente")
        }
        val arquivos = arquivo.listaArquivos(diretorio.diretorioPedidoLocal!!)
        if( arquivos.isEmpty()){
            logger.info("nao existe arquivos para ser importado de ${diretorio.tipoIntegracao.name}")
            return pedidos
        }
        arquivos.forEach { arq ->
            println("carregando arquivo: $arq")
            when(diretorio.tipoIntegracao){
                TipoIntegracao.EMS -> {
                    pedidos.add(TipoIntegracao.toEms(arq).toPedidoPalm())
                }
                TipoIntegracao.CONSYS -> {
                    TipoIntegracao.toConsys(arq).forEach { pedidos.add(it.toPedidoPalm()) }
                }
            }
            this.arquivo.moverArquivo(arq, diretorio.diretorioImportadosLocal + arq.replace(diretorio.diretorioPedidoLocal,""))
        }

        return pedidos
    }


    private fun inserePreVenda(pedidos: List<PedidoPalm>): List<PedidoPalm> {
        var convertidos = mutableListOf<PedidoPalm>()

        pedidos.forEach {
            println(it.IdPedidoPalm.toString() + " esse é o id")
            var result = pedidoPalmRepository.toMovimento(it) ?: ""
            if (result == ""){
                logger.info("erro ao gerar Pre-Venda do pedido de id: ${it.IdPedidoPalm}")
            }else{
                it.SituacaoPedido = "C"
            }
            var resultFim = finalizaMovimento.finaliza(it)
            logger.info("O pedido id: ${it.IdPedidoPalm} gerou o movimento com status = $resultFim")
            convertidos.add(pedidoPalmRepository.findById(it.IdPedidoPalm!!)!!)

        }
        return convertidos
    }


    private fun atualizaStatusRetorno(pedidos: List<PedidoPalm>): List<PedidoPalm> {
        var convertidos = mutableListOf<PedidoPalm>()
        pedidos.forEach { bloqueioMovimentoRepository.executaRegrasTipoGravarRetornos(it)
            convertidos.add(pedidoPalmRepository.findById(it.IdPedidoPalm!!)!!)
        }
        return convertidos
    }


    private fun gerarArquivoDeRetorno(pedidos: List<PedidoPalm>, diretorio: Diretorio) {

        pedidos.forEach {
            when(it.Origem){
                "EMS" -> {
                    val ems = EMS(it)
                    val retorno = ems.gerarRetorno(cnpj, it, diretorio)
                    arquivo.criaArquivo(retorno)
                }
            }
        }

    }

    private fun gerarArquivoDeEstoque(diretorio: Diretorio){

        if(diretorio.diretorioEstoqueLocal == null){
            return
        }
        when(diretorio.tipoIntegracao){
            TipoIntegracao.EMS -> {
               val estoque = geraEstoqueEMS(cnpj, emsRepository, diretorio)
                arquivo.criaArquivo(estoque)
            }
        }
    }



    private fun uploadArquivos(diretorio: Diretorio, tipoEnvio: String){
        when (tipoEnvio){
            "RETORNO" -> {
                val listaArquivos = arquivo.listaArquivos(diretorio.diretorioRetornoLocal ?: throw Exception("Nao existe diretorio de retorno configurado"))
                if (listaArquivos.isEmpty()){
                    logger.info("nao existe arquivos para enviar")
                    return
                }
                val client = ClienteFTP(diretorio.url,21,diretorio.login, diretorio.senha)
                client.abreConexaoFTP()

                listaArquivos.forEach {
                    client.uploadArquivo(it, diretorio?.diretorioRetornoFTP + it.replace(diretorio.diretorioRetornoLocal,"") ?: "/")
                    arquivo.removeArquivo( it)
                }
            }

            "ESTOQUE" -> {
                val listaArquivos = arquivo.listaArquivos(diretorio.diretorioEstoqueLocal ?: throw Exception("Nao existe diretorio de retorno configurado"))
                if (listaArquivos.isEmpty()){
                    logger.info("nao existe arquivos para enviar")
                    return
                }
                val client = ClienteFTP(diretorio.url,21,diretorio.login, diretorio.senha)
                client.abreConexaoFTP()

                listaArquivos.forEach {
                    client.uploadArquivo(it, diretorio?.diretorioEstoqueFTP + it.replace(diretorio.diretorioEstoqueLocal,"") ?: "/")
                    arquivo.removeArquivo( it)
                }
            }
        }
    }




}

