package br.symbiosys.solucoes.cronospharma.processamento.services

import br.symbiosys.solucoes.cronospharma.entidades.diretorios.Diretorio
import br.symbiosys.solucoes.cronospharma.processamento.Arquivo
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.FileReader

@Service
class ArquivoService {

    private final val arquivo = Arquivo()
    val logger = LoggerFactory.getLogger(ArquivoService::class.java)


    fun converterArquivosParaTexto(operador: Diretorio): List<String> {
        try {
            if(operador.diretorioPedidoLocal.isNullOrBlank()){
                logger.info("Nao foi configurado o diretorio de pedidos")
                return emptyList()
            }
            val arquivos = lerDiretorio(operador.diretorioPedidoLocal)
            if (arquivos.isEmpty()){
                logger.info("Nao existe arquivos na pasta de pedidos de ${operador.tipoIntegracao.name}")
                return emptyList()
            }
            val arquivosConvertidos = mutableListOf<String>()
            arquivos.forEach { arq -> arquivosConvertidos.add(arquivoParaTexto(arq)) }
            logger.info("Arquivos convertidos com sucesso para o operador: ${operador.tipoIntegracao.name}")
            return arquivosConvertidos
        } catch (e: Exception) {
            logger.error("erro ao converter arquivos para texto: ${e.message}")
        }
        return emptyList()
    }

    private fun lerDiretorio(caminhoDiretorio: String): List<String> {
       return this.arquivo.listaArquivos(caminhoDiretorio)
    }
    private fun arquivoParaTexto(caminhoArquivo: String): String {
        return FileReader(caminhoArquivo).readText()
    }
}