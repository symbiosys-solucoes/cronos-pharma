package br.symbiosys.solucoes.cronospharma.ftp


import br.symbiosys.solucoes.cronospharma.entidades.diretorios.Diretorio
import org.apache.commons.net.PrintCommandListener
import org.apache.commons.net.ftp.FTPClient
import org.apache.commons.net.ftp.FTPReply
import org.slf4j.LoggerFactory
import java.io.*


/*
* https://www.baeldung.com/java-ftp-client
*  */
class ClienteFTP(
    private val server: String,
    private val port: Int = 21,
    private val user: String,
    private val password: String,
) {
    constructor(d: Diretorio): this(server = d.url, user = d.login, password = d.senha)
    private var ftp: FTPClient = FTPClient()
    private val logger = LoggerFactory.getLogger(ClienteFTP::class.java)



    fun abreConexaoFTP(): FTPClient{
        ftp.addProtocolCommandListener(PrintCommandListener(PrintWriter(System.out)))
        ftp.listHiddenFiles = false


        ftp.connect(server, port)

        val codigoResposta = ftp.replyCode
        val mensagemResposta = ftp.replyString

        if(!FTPReply.isPositiveCompletion(codigoResposta)){
            ftp.disconnect()
            logger.warn("Erro ao conectar ao FTP url: ${server}\n" +
                    "Codigo de Erro: $codigoResposta\n" +
                    "Mensagem de Erro: $mensagemResposta ")
        }
        try {

            ftp.login(user,password)
        } catch (e: IOException){
            logger.warn("Conexao não realizada na url: $server")

        }

        return ftp
    }

    fun fechaConexaoFTP(){
        try {
            ftp.disconnect()
            logger.info("Desconexao realizada com Sucesso na url: ${this.server}")
        } catch (e: IOException){
            logger.warn("Desconexao não realizada na url: ${this.server}")

        }
    }

    fun listaArquivos(path: String): Collection<String> {

        ftp.enterLocalPassiveMode()

        return ftp.listNames(path).filter { it.replace(path, "").length > 2 }.toList()

    }

    fun downloadArquivo(origem: String, destino: String) {
        ftp.enterLocalPassiveMode()
        try {
            logger.info("Baixando o arquivo $origem, no caminho: $destino")
            val arquivo = FileOutputStream(destino)
            if (ftp.retrieveFile(origem, arquivo)) {
                logger.info("arquivo $destino baixado com sucesso!")
                ftp.deleteFile(origem)
            }
            arquivo.close()
        } catch (e: Exception) {
            e.printStackTrace()
            logger.warn("Erro ao realizar download do arquivo")
        }
    }

    fun uploadArquivo(origem: String, destino: String){
        ftp.enterLocalPassiveMode()
        try {
            logger.info("Subindo o arquivo $origem, no caminho: $destino")
            var arquivo = FileInputStream(File(origem))
            ftp.storeFile(destino, arquivo)
            arquivo.close()
        } catch (e: IOException) {
            logger.warn("Erro ao realizar upload do arquivo")
        }

    }


}