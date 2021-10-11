package br.symbiosys.solucoes.cronospharma.cronospharma.ftp




import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockftpserver.fake.FakeFtpServer
import org.mockftpserver.fake.UserAccount
import org.mockftpserver.fake.filesystem.DirectoryEntry
import org.mockftpserver.fake.filesystem.FileEntry
import org.mockftpserver.fake.filesystem.FileSystem
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem
import java.io.File


internal class FtpTest {

//    @Test
//    fun `dado um arquivo remoto deve retornar uma lista de arquivos - meirelles`(){
//       // val instancia = setup()
//        //val clienteFTP = instancia.get("ClienteFTP") as ClienteFTP
//        val clienteFTP = ClienteFTP(server="br698.hostgator.com.br", user = "legrand@meirellesfarma.com.br", password = "Dr4tHZKcv7" )
//        clienteFTP.abreConexaoFTP()
//        val files: Collection<String> = clienteFTP.listaArquivos("/PB/PEDIDO/")
//        clienteFTP.fechaConexaoFTP()
//        //clean(instancia)
//
//        files.forEach { println(it)}
//        assertThat(files).contains("PEDEMS_12520483000134_20211005162156.txt")
//
//
//    }
    @Test
    fun `dado um arquivo remoto deve retornar uma lista de arquivos`(){
        val instancia = setup()
        val clienteFTP = instancia.get("ClienteFTP") as ClienteFTP
        val files: Collection<String> = clienteFTP.listaArquivos("")
        clean(instancia)


        assertThat(files).contains("foobar.txt")
    }

    @Test
    fun `dado um arquivo remoto deve baixar o arquivo`(){
        val instancia = setup()
        val clienteFTP = instancia.get("ClienteFTP") as ClienteFTP
        clienteFTP.downloadArquivo("/foobar.txt", "downloaded_foobar.txt")
        clean(instancia)

        assertThat(File("downloaded_foobar.txt")).exists()
        File("downloaded_foobar.txt").delete()
    }

    @Test
    fun `dado um arquivo local deve subir para o servidor remoto`(){
        val instancia = setup()
        val server: FakeFtpServer = instancia.get("FtpServer") as FakeFtpServer
        val clienteFTP = instancia.get("ClienteFTP") as ClienteFTP
        val arquivo = File.createTempFile("tmp",".txt")
        clienteFTP.uploadArquivo(arquivo.path, "/uploaded.txt")



        assertThat(server.fileSystem.exists("/uploaded.txt")).isTrue()
        clean(instancia)


    }

    //Necessário criar dessa maneira, pois por algum motivo, o ClientFTP nao estava sendo instanciado
    private fun setup(): HashMap<String, Any>{
        val fakeFtpServer = FakeFtpServer()
        fakeFtpServer.addUserAccount(UserAccount("user", "password", "/data"))



        val fileSystem: FileSystem = UnixFakeFileSystem()
        fileSystem.add(DirectoryEntry("/data"))
        fileSystem.add(FileEntry("/data/foobar.txt", "abcdef 123457890"))

        fakeFtpServer.fileSystem = fileSystem
        fakeFtpServer.serverControlPort = 0

        fakeFtpServer.start()

        val clienteFTP = geraClienteFtp(fakeFtpServer.serverControlPort)

        clienteFTP.abreConexaoFTP()
        return hashMapOf<String, Any>("ClienteFTP" to clienteFTP, "FtpServer" to fakeFtpServer)

    }
    private fun clean(map: HashMap<String, Any>){
        val cliente: ClienteFTP = map.get("ClienteFTP") as ClienteFTP
        cliente.fechaConexaoFTP()
        val server: FakeFtpServer = map.get("FtpServer") as FakeFtpServer
        server.stop()
    }
    private fun geraClienteFtp(porta: Int):ClienteFTP {
        return ClienteFTP("localhost", porta, "user", "password")
    }
}