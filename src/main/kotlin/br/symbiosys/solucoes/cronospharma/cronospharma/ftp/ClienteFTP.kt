package br.symbiosys.solucoes.cronospharma.cronospharma.ftp


import br.symbiosys.solucoes.cronospharma.cronospharma.entidades.diretorios.Diretorios
import org.apache.commons.net.ftp.FTPClient
import org.apache.commons.net.ftp.FTPFile
import org.apache.commons.net.ftp.FTPReply
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import java.util.stream.Collectors
import kotlin.reflect.KFunction1
import kotlin.streams.toList


//https://www.baeldung.com/java-ftp-client
class ClienteFTP(
   private val server: String,
   private val port: Int = 21,
   private val user: String,
   private val password: String,
) {
    constructor(d: Diretorios): this(server = d.url, user = d.login, password = d.senha)
    private var ftp: FTPClient = FTPClient()
    private val logger = LoggerFactory.getLogger(ClienteFTP::class.java)

    fun abreConexaoFTP(): FTPClient{
        //val ftp = FTPClient()

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
//            logger.info("Conexao Realizada com sucesso na url: ${server}")
        } catch (e: IOException){
            logger.warn("Conexao não realizada na url: ${server}")
            logger.warn(e.stackTrace.toString())
        }

        return ftp
    }

    fun fechaConexaoFTP(){
       try {
           ftp.disconnect()
           logger.info("Desconexao realizada com Sucesso na url: ${ftp.passiveHost}")
       } catch (e: IOException){
           logger.warn("Desconexao não realizada na url: ${ftp.passiveHost}")
           logger.warn(e.printStackTrace().toString())
       }
    }

    fun listaArquivos(path: String): Collection<String> {

        val files: Array<FTPFile> = ftp.listFiles(path)


        return Arrays.stream(files)
            .map(FTPFile::getName)
            .collect(Collectors.toList())
    }

    fun downloadArquivo(origem: String, destino: String) {
        try {
            logger.info("Baixando o arquivo $origem, no caminho: $destino")
            val arquivo = FileOutputStream(destino)
            ftp.retrieveFile(origem, arquivo)
        } catch (e: IOException) {
            logger.warn("Erro ao realizar download do arquivo: ${e.printStackTrace()}")
        }

    }

    fun uploadArquivo(origem: String, destino: String){

        try {
            logger.info("Subindo o arquivo $origem, no caminho: $destino")
            var arquivo = File(origem)
            ftp.storeFile(destino, FileInputStream(arquivo))
        } catch (e: IOException) {
            logger.warn("Erro ao realizar upload do arquivo: ${e.printStackTrace()}")
        }

    }


}