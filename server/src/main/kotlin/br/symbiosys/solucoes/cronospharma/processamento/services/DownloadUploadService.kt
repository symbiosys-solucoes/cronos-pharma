package br.symbiosys.solucoes.cronospharma.processamento.services

import br.symbiosys.solucoes.cronospharma.entidades.diretorios.Diretorio
import br.symbiosys.solucoes.cronospharma.ftp.ClienteFTP
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service


@Service
class DownloadUploadService {

    private val logger = LoggerFactory.getLogger(DownloadUploadService::class.java)

    fun baixarConteudoDeUmaPasta(operador: Diretorio) {

        try {
            if(operador.usaFTP && operador.diretorioPedidoFTP != null) {
                val clienteFTP = ClienteFTP( server = operador.url, user = operador.login, password = operador.senha)
                logger.info("Abrindo conexao ftp com o servidor: ${operador.url}")
                clienteFTP.abreConexaoFTP()

                logger.info("Checando se existe conteudo para ser baixado na pasta: ${operador.tipoIntegracao.name}")

                val arquivos = clienteFTP.listaArquivos(operador.diretorioPedidoFTP)
                logger.info("Arquivos encontrados: ${arquivos.size}")
                arquivos.forEach {
                    logger.info("Arquivo encontrado: ${it}")
                    val nomeArquivo = operador.diretorioPedidoLocal + it.replace(operador.diretorioPedidoFTP, "")
                    clienteFTP.downloadArquivo(it, nomeArquivo)
                    logger.info("Arquivo baixado: $nomeArquivo")
                }

                clienteFTP.fechaConexaoFTP()
                return

            } else {
                logger.info("Operador não usa FTP, processo sera ignorado")
            }

        } catch (e: Exception) {
            logger.error(e.message, e)
        }
    }
}