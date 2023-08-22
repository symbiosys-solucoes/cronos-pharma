package br.symbiosys.solucoes.cronospharma.cronospharma.entidades.cronos

import CliFor
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class CliForRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {


    val logger = LoggerFactory.getLogger(CliForRepository::class.java)
    fun createFromSymCustomer(symCustomerId: Long): CliFor? {
        try {
          return jdbcTemplate.query(createCliforFromSymCustomer, MapSqlParameterSource("idCustomer", symCustomerId), mapperCliFor).first()
        } catch (e: Exception) {
            logger.error("Erro ao cadastrar cliente no cronos", e)
        }
        return null
    }
    companion object{
        private val mapperCliFor = RowMapper<CliFor> {rs: ResultSet, rowNum: Int ->
            CliFor().apply {
                codCliFor = rs.getString("CodCliFor")
                idEmpresa = rs.getInt("IdEmpresa")
                nomeCliFor = rs.getString("NomeCliFor")
                razaoSocial = rs.getString("RazaoSocial")
                tipoPessoa = rs.getString("TipoPessoa")
                tipoCliente = rs.getString("TipoCliente")
                cpfcgcCliFor = rs.getString("CPFCGCCLIFOR")
                rgieCliFor = rs.getString("RGIECLIFOR")
                contribICMS = rs.getString("ContribICMS")
                inscricaoMunicipal = rs.getString("InscricaoMunicipal")
                tipoLogradouro = rs.getString("TipoLogradouro")
                enderecoCliFor = rs.getString("EnderecoCliFor")
                numeroLogradouro = rs.getString("NumeroLogradouro")
                bairroCliFor = rs.getString("BairroCliFor")
                complemento = rs.getString("Complemento")
                pontoReferencia = rs.getString("PontoReferencia")
                email = rs.getString("email")
                cepCliFor = rs.getString("CEPCLIFor")
                idCidade = rs.getInt("IdCidade")
                idRegiao = rs.getInt("IdRegiao")
                intinerarioVisita = rs.getDouble("IntinerarioVisita")
                correspondencia = rs.getString("Correspondencia")
                ufCliFor = rs.getString("UFCLIFor")
                regimeTributario = rs.getString("RegimeTributario")
                foneCliFor = rs.getString("FoneCliFor")
                faxCliFor = rs.getString("FaxCliFor")
                celCliFor = rs.getString("CelCliFor")
                cargoCli = rs.getString("CARGOCLI")
                conjuge = rs.getString("CONJUGE")
                cpfConjuge = rs.getString("CPFCONJUGE")
                profissaoConjuge = rs.getString("ProfissaoConjuge")
                estadoCivil = rs.getString("ESTADOCIVIL")
                ctps = rs.getString("CTPS")
                dtNascCli = rs.getTimestamp("DTNASCCLI")?.toLocalDateTime()
                sexo = rs.getString("Sexo")
                naturalidade = rs.getString("Naturalidade")
                nacionalidade = rs.getString("Nacionalidade")
                trabalhoCli = rs.getString("TrabalhoCli")
                enderecoTrab = rs.getString("EnderecoTrab")
                foneTrabalho = rs.getString("FoneTrabalho")
                rendaMensal = rs.getDouble("RendaMensal")
                tempoServico = rs.getString("TempoServico")
                nomePessoaRef = rs.getString("NomePessoaRef")
                enderecoPessoaRef = rs.getString("EnderecoPessoaRef")
                fonePessoaRef = rs.getString("FonePessoaRef")
                nomeMae = rs.getString("NomeMae")
                nomePai = rs.getString("NomePai")
                observacao = rs.getString("OBSERVACAO")
                listaNegra = rs.getString("LISTANEGRA")
                nomeFiador = rs.getString("NomeFiador")
                enderecoFiador = rs.getString("EnderecoFiador")
                cidadeFiador = rs.getString("CidadeFiador")
                ufFiador = rs.getString("UFFiador")
                foneFiador = rs.getString("FoneFiador")
                dtNascFiador = rs.getTimestamp("DtNascFiador")?.toLocalDateTime()
                rgFiador = rs.getString("RgFiador")
                cpfFiador = rs.getString("CpfFiador")
                nomePaiFiador = rs.getString("NomePaiFiador")
                nomeMaeFiador = rs.getString("NomeMaeFiador")
                bensImoveis = rs.getString("BensImoveis")
                limiteCredito = rs.getDouble("LimiteCredito")
                refBancoNome = rs.getString("RefBancoNome")
                refBancoAgencia = rs.getString("RefBancoAgencia")
                refBancoConta = rs.getString("RefBancoConta")
                refBancoTitular = rs.getString("RefBancoTitular")
                refBancoData = rs.getTimestamp("RefBancoData")?.toLocalDateTime()
                refBancoCpfTitular = rs.getString("RefBancoCPFTitular")
                refBancoRgTitular = rs.getString("RefBancoRgTitular")
                refBancoEnderecoTitular = rs.getString("RefBancoEnderecoTitular")
                refBancoTelefoneTitular = rs.getString("RefBancoTelefoneTitular")
                refBancoObservacoes = rs.getString("RefBancoObservacoes")
                refComNome = rs.getString("RefComNome")
                refComTelefone = rs.getString("RefComTelefone")
                refComNome2 = rs.getString("RefComNome2")
                refComTelefone2 = rs.getString("RefComTelefone2")
                refComNome3 = rs.getString("RefComNome3")
                refComTelefone3 = rs.getString("RefComTelefone3")
                avDataConsulta = rs.getTimestamp("AvDataConsulta")?.toLocalDateTime()
                avSistema = rs.getString("AvSistema")
                avNumConsulta = rs.getString("AvNumConsulta")
                avSituacao = rs.getString("AvSituacao")
                avNumChequeDev = rs.getString("AvNumChequeDev")
                avDtChequeDev = rs.getTimestamp("AvDtChequeDev")?.toLocalDateTime()
                avAnotacoes = rs.getString("AvAnotacoes")
                cfMargemLucroBruto = rs.getDouble("cfMargemLucroBruto")
                cfMargemLucroBruto2 = rs.getDouble("cfMargemLucroBruto2")
                cfMargemLucroBruto3 = rs.getDouble("cfMargemLucroBruto3")
                dataRenovacao = rs.getTimestamp("DataRenovacao")?.toLocalDateTime()
                campoAlfaOp1 = rs.getString("CampoAlfaOp1")
                campoAlfaOp2 = rs.getString("CampoAlfaOp2")
                valorOp1 = rs.getDouble("ValorOp1")
                dataOp1 = rs.getTimestamp("DataOp1")?.toLocalDateTime()
                dataUltMovimento = rs.getTimestamp("DataUltMovimento")?.toLocalDateTime()
                inativo = rs.getString("Inativo")
                idPlanoConta = rs.getInt("IdPlanoConta")
                codPortador = rs.getString("CodPortador")
                buscarPreco = rs.getString("BuscarPreco")
                codCondPag = rs.getString("CodCondPag")
                codFunc = rs.getString("CodFunc")
                codFunc2 = rs.getString("CodFunc2")
                idCategoriaCliFor = rs.getInt("IdCategoriaCliFor")
                nomeContato = rs.getString("NomeContato")
                telefoneContato = rs.getString("TelefoneContato")
                emailContato = rs.getString("EmailContato")
                rgContato = rs.getString("RgContato")
                cpfContato = rs.getString("CpfContato")
                dtNascContato = rs.getTimestamp("DtNascContato")?.toLocalDateTime()
                dataInicioAtividades = rs.getTimestamp("DataInicioAtividades")?.toLocalDateTime()
                msgPadraoMovimento = rs.getString("MsgPadraoMovimento")
                idTabCliFor1 = rs.getInt("IdTabCliFor1")
                idTabCliFor2 = rs.getInt("IdTabCliFor2")
                idTabCliFor3 = rs.getInt("IdTabCliFor3")
                enderecoCliForPagAtivo = rs.getString("EnderecoCliForPagAtivo")
                tipoLogradouroPag = rs.getString("TipoLogradouroPag")
                enderecoCliForPag = rs.getString("EnderecoCliForPag")
                numeroLogradouroPag = rs.getString("NumeroLogradouroPag")
                bairroCliForPag = rs.getString("BairroCliForPag")
                complementoPag = rs.getString("ComplementoPag")
                emailPag = rs.getString("EmailPag")
                cepCliForPag = rs.getString("CEPCliForPag")
                idCidadePag = rs.getInt("IdCidadePag")
                nomeCidadePag = rs.getString("NomeCidadePag")
                ufCliForPag = rs.getString("UFCLIForPag")
                foneCliForPag = rs.getString("FoneCliForPag")
                faxCliForPag = rs.getString("FaxCliForPag")
                celCliForPag = rs.getString("CelCliForPag")
                enderecoCliForEntAtivo = rs.getString("EnderecoCliForEntAtivo")
                tipoLogradouroEnt = rs.getString("TipoLogradouroEnt")
                enderecoCliForEnt = rs.getString("EnderecoCliForEnt")
                numeroLogradouroEnt = rs.getString("NumeroLogradouroEnt")
                bairroCliForEnt = rs.getString("BairroCliForEnt")
                complementoEnt = rs.getString("ComplementoEnt")
                emailEnt = rs.getString("EmailEnt")
                cepCliForEnt = rs.getString("CEPCliForEnt")
                idCidadeEnt = rs.getInt("IdCidadeEnt")
                nomeCidadeEnt = rs.getString("NomeCidadeEnt")
                ufCliForEnt = rs.getString("UFCLIForEnt")
                foneCliForEnt = rs.getString("FoneCliForEnt")
                faxCliForEnt = rs.getString("FaxCliForEnt")
                celCliForEnt = rs.getString("CelCliForEnt")
                usuarioWeb = rs.getString("UsuarioWeb")
                senhaWeb = rs.getString("SenhaWeb")
                usuarioWebAtivo = rs.getString("UsuarioWebAtivo")
                anvisaNumero = rs.getString("AnvisaNumero")
                anvisaVencimento = rs.getString("AnvisaVencimento")
                anvisaDtVencimento = rs.getTimestamp("AnvisaDtVencimento")?.toLocalDateTime()
                covisaNumero = rs.getString("CovisaNumero")
                covisaVencimento = rs.getString("CovisaVencimento")
                covisaDtVencimento = rs.getTimestamp("CovisaDtVencimento")?.toLocalDateTime()
                farmaceutico = rs.getString("Farmaceutico")
                compraPsicotropicos = rs.getString("CompraPsicotropicos")
                codContabil = rs.getString("CodContabil")
                codContrapartida = rs.getString("CodContrapartida")
                percDescFinanc = rs.getDouble("PercDescFinanc")
                percDescFinancFixo = rs.getDouble("PercDescFinancFixo")
                chaveSeguranca = rs.getString("ChaveSeguranca")
                posLatitudeCad = rs.getFloat("PosLatitudeCad")
                posLongitudeCad = rs.getFloat("PosLongitudeCad")
                cnae = rs.getString("CNAE")
                produtorRural = rs.getString("ProdutorRural")
                codSUFRAMA = rs.getString("CodSUFRAMA")
                syncDate = rs.getTimestamp("SyncDate")?.toLocalDateTime()
                dataOperacao = rs.getTimestamp("DataOperacao")?.toLocalDateTime()
                idUsuario = rs.getString("IdUsuario")
                webSite = rs.getString("WebSite")
                refBancoId = rs.getString("RefBancoId")
                refBancoAgenciaDV = rs.getString("RefBancoAgenciaDV")
                refBancoContaDV = rs.getString("RefBancoContaDV")
                refBancoContaTp = rs.getString("RefBancoContaTp")
                formaPag = rs.getString("FormaPag")
                chavePix = rs.getString("ChavePix")
                ramoAtividade = rs.getInt("RamoAtividade")
                orgaoPublico = rs.getString("OrgaoPublico")
                contribISS = rs.getString("ContribISS")
                porteEmpresa = rs.getString("PorteEmpresa")



            }
        }
        val createCliforFromSymCustomer = "" +
                "DECLARE @NEWCODCLIFOR VARCHAR(20)\n" +
                "SET NOCOUNT ON\n" +
                "EXEC @NEWCODCLIFOR = dbo.sp_NextId 'Clientes'\n" +
                "INSERT INTO Cli_For\n" +
                "(CodCliFor, IdEmpresa, NomeCliFor, RazaoSocial, TipoPessoa, TipoCliente, CPFCGCCLIFOR, RGIECLIFOR,\n" +
                "ContribICMS, EnderecoCliFor, NumeroLogradouro, BairroCliFor, CepCliFor, IdCidade,\n" +
                "IdRegiao, UfCliFor, FoneCliFor,  CelCliFor, Inativo, CodPortador, BuscarPreco, CodCondPag, IdCategoriaCliFor, \n" +
                "DataOperacao, IdUsuario, WebSite)\n" +
                "\n" +
                "SELECT CODCLIFOR = 'C' + dbo.fn_PreencherZeros(@NEWCODCLIFOR,5), IDEMPRESA = 1 , FANTASIA = nome_fantasia, RAZAO = razao_social, TIPO = CASE WHEN LEN(cpf_cnpj) > 11 THEN 'J' ELSE 'F' END,\n" +
                "TIPOCLIENTE= 'C', CPFCNPJ = CASE WHEN LEN(cpf_cnpj) > 11 THEN STUFF(STUFF(STUFF(STUFF(cpf_cnpj, 3, 0, '.'), 7, 0, '.'), 11, 0, '/'), 16, 0, '-') ELSE \n" +
                "STUFF(STUFF(STUFF(cpf_cnpj, 4, 0, '.'), 8, 0, '.'), 12, 0, '-') END, IE = '', ICMS = 'N', endereco, endereco2, bairro,  cep,\n" +
                "IDCIDADE = (SELECT IdCidade  FROM Cidade c WHERE Cidade LIKE '%'+ zsym_clientes.cidade +'%' AND UF = zsym_clientes.uf  ), IDREGIAO = 1,\n" +
                "UF, telefone, telefone2, INATIVO = 'N', PORTADOR = (SELECT CodPortador FROM Portador p WHERE NomePortador LIKE '%' +zsym_clientes.metodo_pagamento  +'%'),\n" +
                "TABELAPRECO = '0', CONDICAO = (SELECT CodCondPag FROM CondPag cp  WHERE cp.CondPag  LIKE '%' +zsym_clientes.condicao_pagamento  +'%'),\n" +
                "CATEGORIA = 1, DTOPERACAO = GETDATE(), USUARIO = 'SYM', website\n" +
                "\n" +
                "FROM zsym_clientes where id = :idCustomer AND cpf_cnpj is not null\n" +
                "UPDATE zsym_clientes set codigo_cronos = 'C' + @NEWCODCLIFOR WHERE id = :idCustomer\n" +
                "SET NOCOUNT OFF\n" +
                "\n" +
                "select * from Cli_For cf where CodCliFor = 'C' + @NEWCODCLIFOR"

        val selectCliFor = "" +
                "SELECT CodCliFor, IdEmpresa, NomeCliFor, RazaoSocial, TipoPessoa, TipoCliente, CPFCGCCLIFOR, RGIECLIFOR, ContribICMS," +
                " InscricaoMunicipal, TipoLogradouro, EnderecoCliFor, NumeroLogradouro, BairroCliFor, Complemento, PontoReferencia," +
                " email, CepCliFor, IdCidade, IdRegiao, IntinerarioVisita, Correspondencia, UfCliFor, RegimeTributario, FoneCliFor," +
                " FaxCliFor, CelCliFor, CARGOCLI, CONJUGE, CpfConjuge, ProfissaoConjuge, ESTADOCIVIL, CTPS, DTNASCCLI, Sexo, " +
                "Naturalidade, Nacionalidade, TrabalhoCli, EnderecoTrab, FoneTrabalho, RendaMensal, TempoServico, NOMEPESSOAREF, " +
                "ENDERECOPESSOAREF, FONEPESSOAREF, NOMEMAE, NOMEPAI, OBSERVACAO, LISTANEGRA, NomeFiador, EnderecoFiador, CidadeFiador," +
                " UfFiador, FoneFiador, DtNascFiador, RgFiador, CpfFiador, NomePaiFiador, NomeMaeFiador, BensImoveis, LimiteCredito," +
                " RefBancoNome, RefBancoAgencia, RefBancoConta, RefBancoTitular, RefBancoData, RefBancoCPFTitular, RefBancoRgTitular," +
                " RefBancoEnderecoTitular, RefBancoTelefoneTitular, RefBancoObservacoes, RefComNome, RefComTelefone, RefComNome2, " +
                "RefComTelefone2, RefComNome3, RefComTelefone3, AvDataConsulta, AvSistema, AvNumConsulta, AvSituacao, AvNumChequeDev, " +
                "AvDtChequeDev, AvAnotacoes, cfMargemLucroBruto, cfMargemLucroBruto2, cfMargemLucroBruto3, DataRenovacao, CampoAlfaOp1, " +
                "CampoAlfaOp2, ValorOp1, DataOp1, DataUltMovimento, Inativo, IdPlanoConta, CodPortador, BuscarPreco, CodCondPag, CodFunc, " +
                "CodFunc2, IdCategoriaCliFor, NomeContato, TelefoneContato, EmailContato, RGContato, CPFContato, DtNascContato, " +
                "DataInicioAtividades, MsgPadraoMovimento, IdTabCliFor1, IdTabCliFor2, IdTabCliFor3, EnderecoCliForPagAtivo, TipoLogradouroPag, " +
                "EnderecoCliForPag, NumeroLogradouroPag, BairroCliForPag, ComplementoPag, EmailPag, CepCliForPag, IdCidadePag, NomeCidadePag, " +
                "UfCliForPag, FoneCliForPag, FaxCliForPag, CelCliForPag, EnderecoCliForEntAtivo, TipoLogradouroEnt, EnderecoCliForEnt, " +
                "NumeroLogradouroEnt, BairroCliForEnt, ComplementoEnt, EmailEnt, CepCliForEnt, IdCidadeEnt, NomeCidadeEnt, UfCliForEnt, " +
                "FoneCliForEnt, PercComissaoVendedor, FaxCliForEnt, CelCliForEnt, UsuarioWeb, SenhaWeb, UsuarioWebAtivo, AnvisaNumero, " +
                "AnvisaVencimento, AnvisaDtVencimento, CovisaNumero, CovisaVencimento, CovisaDtVencimento, Farmaceutico, CompraPsicotropicos, " +
                "CodContabil, CodContrapartida, PercDescFinanc, PercDescFinancFixo, ChaveSeguranca, PosLatitudeCad, PosLongitudeCad, CNAE, " +
                "ProdutorRural, CodSUFRAMA, SyncDate, DataOperacao, IdUsuario, WebSite, RefBancoId, RefBancoAgenciaDV, RefBancoContaDV, " +
                "RefBancoContaTp, FormaPag, ChavePix, RamoAtividade, OrgaoPublico, ContribISS, PorteEmpresa\n" +
                "FROM Cli_For;\n"
    }

}
