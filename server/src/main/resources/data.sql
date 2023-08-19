
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'ZProdutosCompl') AND type in (N'U'))
BEGIN
CREATE TABLE ZProdutosCompl
(
    sym_ativo VARCHAR(1),
    sym_usaFTP VARCHAR(1),
    sym_url VARCHAR(255),
    sym_login VARCHAR(255),
    sym_senha VARCHAR(255),
    sym_dir_ped_ftp VARCHAR(255),
    sym_dir_ret_ftp VARCHAR(255),
    sym_dir_ped_local VARCHAR(255),
    sym_dir_ret_local VARCHAR(255),
    sym_dir_imp_local VARCHAR(255),
    CodTd_sym_tipo VARCHAR(255),
    sym_dir_est_ftp varchar(255),
    sym_dir_est_local VARCHAR(255),
    sym_dir_preco_ftp VARCHAR(255),
    sym_dir_preco_local VARCHAR(255),
    IdProduto INT
);

END;

IF NOT EXISTS (
    SELECT *
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'ZProdutosCompl'
    AND COLUMN_NAME = 'sym_ativo'
)
BEGIN
ALTER TABLE ZProdutosCompl
    ADD sym_ativo VARCHAR(1);

END;
IF NOT EXISTS (
    SELECT *
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'ZProdutosCompl'
    AND COLUMN_NAME = 'sym_usaFTP'
)
BEGIN
ALTER TABLE ZProdutosCompl
    ADD sym_usaFTP VARCHAR(1);

END;
IF NOT EXISTS (
    SELECT *
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'ZProdutosCompl'
    AND COLUMN_NAME = 'sym_url'
)
BEGIN
ALTER TABLE ZProdutosCompl
    ADD sym_url VARCHAR(255);

END;
IF NOT EXISTS (
    SELECT *
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'ZProdutosCompl'
    AND COLUMN_NAME = 'sym_login'
)
BEGIN
ALTER TABLE ZProdutosCompl
    ADD sym_login VARCHAR(255);

END;
IF NOT EXISTS (
    SELECT *
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'ZProdutosCompl'
    AND COLUMN_NAME = 'sym_senha'
)
BEGIN
ALTER TABLE ZProdutosCompl
    ADD sym_senha VARCHAR(255);

END;
IF NOT EXISTS (
    SELECT *
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'ZProdutosCompl'
    AND COLUMN_NAME = 'sym_dir_ped_ftp'
)
BEGIN
ALTER TABLE ZProdutosCompl
    ADD sym_dir_ped_ftp VARCHAR(255);

END;
IF NOT EXISTS (
    SELECT *
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'ZProdutosCompl'
    AND COLUMN_NAME = 'sym_dir_ret_ftp'
)
BEGIN
ALTER TABLE ZProdutosCompl
    ADD sym_dir_ret_ftp VARCHAR(255);

END;
IF NOT EXISTS (
    SELECT *
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'ZProdutosCompl'
    AND COLUMN_NAME = 'sym_dir_ped_local'
)
BEGIN
ALTER TABLE ZProdutosCompl
    ADD sym_dir_ped_local VARCHAR(255);

END;
IF NOT EXISTS (
    SELECT *
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'ZProdutosCompl'
    AND COLUMN_NAME = 'sym_dir_ret_local'
)
BEGIN
ALTER TABLE ZProdutosCompl
    ADD sym_dir_ret_local VARCHAR(255);

END;
IF NOT EXISTS (
    SELECT *
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'ZProdutosCompl'
    AND COLUMN_NAME = 'sym_dir_imp_local'
)
BEGIN
ALTER TABLE ZProdutosCompl
    ADD sym_dir_imp_local VARCHAR(255);

END;
IF NOT EXISTS (
    SELECT *
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'ZProdutosCompl'
    AND COLUMN_NAME = 'CodTd_sym_tipo'
)
BEGIN
ALTER TABLE ZProdutosCompl
    ADD CodTd_sym_tipo VARCHAR(255);

END;
IF NOT EXISTS (
    SELECT *
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'ZProdutosCompl'
    AND COLUMN_NAME = 'sym_dir_est_ftp'
)
BEGIN
ALTER TABLE ZProdutosCompl
    ADD sym_dir_est_ftp VARCHAR(255);

END;
IF NOT EXISTS (
    SELECT *
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'ZProdutosCompl'
    AND COLUMN_NAME = 'sym_dir_est_local'
)
BEGIN
ALTER TABLE ZProdutosCompl
    ADD sym_dir_est_local VARCHAR(255);

END;
IF NOT EXISTS (
    SELECT *
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'ZProdutosCompl'
    AND COLUMN_NAME = 'sym_dir_preco_ftp'
)
BEGIN
ALTER TABLE ZProdutosCompl
    ADD sym_dir_preco_ftp VARCHAR(255);

END;
IF NOT EXISTS (
    SELECT *
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'ZProdutosCompl'
    AND COLUMN_NAME = 'sym_dir_preco_local'
)
BEGIN
ALTER TABLE ZProdutosCompl
    ADD sym_dir_preco_local VARCHAR(255);

END;















-- TRIGGER CLIFOR
IF  OBJECT_ID('AuditTrigger_Cli_For', 'TR') IS  NULL
BEGIN
EXEC dbo.sp_executesql @statement = N'	
	CREATE TRIGGER dbo.AuditTrigger_Cli_For
	ON dbo.Cli_For 
	AFTER DELETE, INSERT, UPDATE
	AS
	BEGIN
	    SET NOCOUNT ON;
	   
	        DECLARE @Operation NVARCHAR(10), @CODCLI VARCHAR(20);
	
	        IF EXISTS (SELECT * FROM deleted)
	        BEGIN
	            IF EXISTS (SELECT * FROM inserted)
	                SET @Operation = ''UPDATE'';
	            ELSE
	                SET @Operation = ''DELETE'';
	        END
	        ELSE
	            SET @Operation = ''INSERT'';
			SELECT @CODCLI = CodCliFor FROM deleted
	        DECLARE @OldRecord NVARCHAR(MAX) = (SELECT     CodCliFor,
	    IdEmpresa,
	    NomeCliFor,
	    RazaoSocial,
	    TipoPessoa,
	    TipoCliente,
	    CpfcgcCliFor,
	    RgieCliFor,
	    ContribICMS,
	    InscricaoMunicipal,
	    TipoLogradouro,
	    EnderecoCliFor,
	    NumeroLogradouro,
	    BairroCliFor,
	    Complemento,
	    PontoReferencia,
	    Email,
	    CepCliFor,
	    IdCidade,
	    IdRegiao,
	    IntinerarioVisita,
	    Correspondencia,
	    UfCliFor,
	    RegimeTributario,
	    FoneCliFor,
	    FaxCliFor,
	    CelCliFor,
	    CargoCli,
	    Conjuge,
	    CpfConjuge,
	    ProfissaoConjuge,
	    EstadoCivil,
	    Ctps,
	    DtNascCli,
	    Sexo,
	    Naturalidade,
	    Nacionalidade,
	    TrabalhoCli,
	    EnderecoTrab,
	    FoneTrabalho,
	    RendaMensal,
	    TempoServico,
	    NomePessoaRef,
	    EnderecoPessoaRef,
	    FonePessoaRef,
	    NomeMae,
	    NomePai,
	    ListaNegra,
	    NomeFiador,
	    EnderecoFiador,
	    CidadeFiador,
	    UfFiador,
	    FoneFiador,
	    DtNascFiador,
	    RgFiador,
	    CpfFiador,
	    NomePaiFiador,
	    NomeMaeFiador,
	    BensImoveis,
	    LimiteCredito,
	    RefBancoNome,
	    RefBancoAgencia,
	    RefBancoConta,
	    RefBancoTitular,
	    RefBancoData,
	    RefBancoCpfTitular,
	    RefBancoRgTitular,
	    RefBancoEnderecoTitular,
	    RefBancoTelefoneTitular,
	    RefBancoObservacoes,
	    RefComNome,
	    RefComTelefone,
	    RefComNome2,
	    RefComTelefone2,
	    RefComNome3,
	    RefComTelefone3,
	    AvDataConsulta,
	    AvSistema,
	    AvNumConsulta,
	    AvSituacao,
	    AvNumChequeDev,
	    AvDtChequeDev,
	    AvAnotacoes,
	    CfMargemLucroBruto,
	    CfMargemLucroBruto2,
	    CfMargemLucroBruto3,
	    DataRenovacao,
	    CampoAlfaOp1,
	    CampoAlfaOp2,
	    ValorOp1,
	    DataOp1,
	    DataUltMovimento,
	    Inativo,
	    IdPlanoConta,
	    CodPortador,
	    BuscarPreco,
	    CodCondPag,
	    CodFunc,
	    CodFunc2,
	    IdCategoriaCliFor,
	    NomeContato,
	    TelefoneContato,
	    EmailContato,
	    RgContato,
	    CpfContato,
	    DtNascContato,
	    DataInicioAtividades,
	    MsgPadraoMovimento,
	    IdTabCliFor1,
	    IdTabCliFor2,
	    IdTabCliFor3,
	    EnderecoCliForPagAtivo,
	    TipoLogradouroPag,
	    EnderecoCliForPag,
	    NumeroLogradouroPag,
	    BairroCliForPag,
	    ComplementoPag,
	    EmailPag,
	    CepCliForPag,
	    IdCidadePag,
	    NomeCidadePag,
	    UfCliForPag,
	    FoneCliForPag,
	    FaxCliForPag,
	    CelCliForPag,
	    EnderecoCliForEntAtivo,
	    TipoLogradouroEnt,
	    EnderecoCliForEnt,
	    NumeroLogradouroEnt,
	    BairroCliForEnt,
	    ComplementoEnt,
	    EmailEnt,
	    CepCliForEnt,
	    IdCidadeEnt,
	    NomeCidadeEnt,
	    UfCliForEnt,
	    FoneCliForEnt,
	    PercComissaoVendedor,
	    FaxCliForEnt,
	    CelCliForEnt,
	    UsuarioWeb,
	    SenhaWeb,
	    UsuarioWebAtivo,
	    AnvisaNumero,
	    AnvisaVencimento,
	    AnvisaDtVencimento,
	    CovisaNumero,
	    CovisaVencimento,
	    CovisaDtVencimento,
	    Farmaceutico,
	    CompraPsicotropicos,
	    CodContabil,
	    CodContrapartida,
	    PercDescFinanc,
	    PercDescFinancFixo,
	    ChaveSeguranca,
	    PosLatitudeCad,
	    PosLongitudeCad,
	    Cnae,
	    ProdutorRural,
	    CodSUFRAMA,
	    SyncDate,
	    DataOperacao,
	    IdUsuario,
	    WebSite,
	    RefBancoId,
	    RefBancoAgenciaDV,
	    RefBancoContaDV,
	    RefBancoContaTp,
	    FormaPag,
	    ChavePix,
	    RamoAtividade,
	    OrgaoPublico,
	    ContribISS,
	    PorteEmpresa FROM deleted FOR JSON AUTO, WITHOUT_ARRAY_WRAPPER);
	        DECLARE @NewRecord NVARCHAR(MAX) = (SELECT     CodCliFor,
	    IdEmpresa,
	    NomeCliFor,
	    RazaoSocial,
	    TipoPessoa,
	    TipoCliente,
	    CpfcgcCliFor,
	    RgieCliFor,
	    ContribICMS,
	    InscricaoMunicipal,
	    TipoLogradouro,
	    EnderecoCliFor,
	    NumeroLogradouro,
	    BairroCliFor,
	    Complemento,
	    PontoReferencia,
	    Email,
	    CepCliFor,
	    IdCidade,
	    IdRegiao,
	    IntinerarioVisita,
	    Correspondencia,
	    UfCliFor,
	    RegimeTributario,
	    FoneCliFor,
	    FaxCliFor,
	    CelCliFor,
	    CargoCli,
	    Conjuge,
	    CpfConjuge,
	    ProfissaoConjuge,
	    EstadoCivil,
	    Ctps,
	    DtNascCli,
	    Sexo,
	    Naturalidade,
	    Nacionalidade,
	    TrabalhoCli,
	    EnderecoTrab,
	    FoneTrabalho,
	    RendaMensal,
	    TempoServico,
	    NomePessoaRef,
	    EnderecoPessoaRef,
	    FonePessoaRef,
	    NomeMae,
	    NomePai,
	    ListaNegra,
	    NomeFiador,
	    EnderecoFiador,
	    CidadeFiador,
	    UfFiador,
	    FoneFiador,
	    DtNascFiador,
	    RgFiador,
	    CpfFiador,
	    NomePaiFiador,
	    NomeMaeFiador,
	    BensImoveis,
	    LimiteCredito,
	    RefBancoNome,
	    RefBancoAgencia,
	    RefBancoConta,
	    RefBancoTitular,
	    RefBancoData,
	    RefBancoCpfTitular,
	    RefBancoRgTitular,
	    RefBancoEnderecoTitular,
	    RefBancoTelefoneTitular,
	    RefBancoObservacoes,
	    RefComNome,
	    RefComTelefone,
	    RefComNome2,
	    RefComTelefone2,
	    RefComNome3,
	    RefComTelefone3,
	    AvDataConsulta,
	    AvSistema,
	    AvNumConsulta,
	    AvSituacao,
	    AvNumChequeDev,
	    AvDtChequeDev,
	    AvAnotacoes,
	    CfMargemLucroBruto,
	    CfMargemLucroBruto2,
	    CfMargemLucroBruto3,
	    DataRenovacao,
	    CampoAlfaOp1,
	    CampoAlfaOp2,
	    ValorOp1,
	    DataOp1,
	    DataUltMovimento,
	    Inativo,
	    IdPlanoConta,
	    CodPortador,
	    BuscarPreco,
	    CodCondPag,
	    CodFunc,
	    CodFunc2,
	    IdCategoriaCliFor,
	    NomeContato,
	    TelefoneContato,
	    EmailContato,
	    RgContato,
	    CpfContato,
	    DtNascContato,
	    DataInicioAtividades,
	    MsgPadraoMovimento,
	    IdTabCliFor1,
	    IdTabCliFor2,
	    IdTabCliFor3,
	    EnderecoCliForPagAtivo,
	    TipoLogradouroPag,
	    EnderecoCliForPag,
	    NumeroLogradouroPag,
	    BairroCliForPag,
	    ComplementoPag,
	    EmailPag,
	    CepCliForPag,
	    IdCidadePag,
	    NomeCidadePag,
	    UfCliForPag,
	    FoneCliForPag,
	    FaxCliForPag,
	    CelCliForPag,
	    EnderecoCliForEntAtivo,
	    TipoLogradouroEnt,
	    EnderecoCliForEnt,
	    NumeroLogradouroEnt,
	    BairroCliForEnt,
	    ComplementoEnt,
	    EmailEnt,
	    CepCliForEnt,
	    IdCidadeEnt,
	    NomeCidadeEnt,
	    UfCliForEnt,
	    FoneCliForEnt,
	    PercComissaoVendedor,
	    FaxCliForEnt,
	    CelCliForEnt,
	    UsuarioWeb,
	    SenhaWeb,
	    UsuarioWebAtivo,
	    AnvisaNumero,
	    AnvisaVencimento,
	    AnvisaDtVencimento,
	    CovisaNumero,
	    CovisaVencimento,
	    CovisaDtVencimento,
	    Farmaceutico,
	    CompraPsicotropicos,
	    CodContabil,
	    CodContrapartida,
	    PercDescFinanc,
	    PercDescFinancFixo,
	    ChaveSeguranca,
	    PosLatitudeCad,
	    PosLongitudeCad,
	    Cnae,
	    ProdutorRural,
	    CodSUFRAMA,
	    SyncDate,
	    DataOperacao,
	    IdUsuario,
	    WebSite,
	    RefBancoId,
	    RefBancoAgenciaDV,
	    RefBancoContaDV,
	    RefBancoContaTp,
	    FormaPag,
	    ChavePix,
	    RamoAtividade,
	    OrgaoPublico,
	    ContribISS,
	    PorteEmpresa FROM inserted FOR JSON AUTO, WITHOUT_ARRAY_WRAPPER);
	
	        INSERT INTO dbo.zsym_eventos (data_evento, id_registro, new_register_as_json, old_register_as_json, tabela, tipo, usuario)
	        VALUES (GETDATE(), @CODCLI, @NewRecord, @OldRecord, ''Cli_For'', @Operation,CURRENT_USER);
	
	END;'
END;

-- TRIGGER CPR
IF  OBJECT_ID('AuditTrigger_CPR', 'TR') IS  NULL
BEGIN
EXEC dbo.sp_executesql @statement = N'

	CREATE TRIGGER dbo.AuditTrigger_CPR
	ON dbo.CPR
	AFTER DELETE, INSERT, UPDATE
	AS
	BEGIN
	    SET NOCOUNT ON;
	   	IF EXISTS (SELECT 1 FROM inserted CPR INNER JOIN Movimento m ON CPR.IdMov = m.IdMov  WHERE m.IdPedidoPalm IS NOT NULL)
	   		BEGIN
				DECLARE @Operation NVARCHAR(10), @ID VARCHAR(20);

		        IF EXISTS (SELECT * FROM deleted)
		        BEGIN
		            IF EXISTS (SELECT * FROM inserted)
		                SET @Operation = ''UPDATE'';
		            ELSE
		                SET @Operation = ''DELETE'';
		        END
		        ELSE
		            SET @Operation = ''INSERT'';
				SELECT @ID = ISNULL((SELECT IdCpr FROM deleted), (SELECT IdCpr FROM inserted) )
		        DECLARE @OldRecord NVARCHAR(MAX) = (SELECT  *   FROM deleted FOR JSON AUTO, WITHOUT_ARRAY_WRAPPER);
		        DECLARE @NewRecord NVARCHAR(MAX) = (SELECT   *  FROM inserted FOR JSON AUTO, WITHOUT_ARRAY_WRAPPER);

		        INSERT INTO dbo.zsym_eventos (data_evento, id_registro, new_register_as_json, old_register_as_json, tabela, tipo, usuario)
		        VALUES (GETDATE(), @ID, @NewRecord, @OldRecord, ''CPR'', @Operation, CURRENT_USER);

	   		END




	END;'
END;

-- TRIGGER Movimento

IF  OBJECT_ID('AuditTrigger_Movimento', 'TR') IS  NULL
BEGIN
EXEC dbo.sp_executesql @statement = N'	CREATE TRIGGER dbo.AuditTrigger_Movimento
	ON dbo.Movimento
	AFTER DELETE, INSERT, UPDATE
	AS
	BEGIN
	    SET NOCOUNT ON;
	   	IF EXISTS (SELECT 1 FROM inserted  WHERE IdPedidoPalm IS NOT NULL)
	   		BEGIN
				DECLARE @Operation NVARCHAR(10), @ID VARCHAR(20);

		        IF EXISTS (SELECT * FROM deleted)
		        BEGIN
		            IF EXISTS (SELECT * FROM inserted)
		                SET @Operation = ''UPDATE'';
		            ELSE
		                SET @Operation = ''DELETE'';
		        END
		        ELSE
		            SET @Operation = ''INSERT'';
				SELECT @ID = ISNULL((SELECT IdMov FROM deleted), (SELECT IdMov FROM inserted) )
		        DECLARE @OldRecord NVARCHAR(MAX) = (SELECT  *   FROM deleted FOR JSON AUTO, WITHOUT_ARRAY_WRAPPER);
		        DECLARE @NewRecord NVARCHAR(MAX) = (SELECT   *  FROM inserted FOR JSON AUTO, WITHOUT_ARRAY_WRAPPER);

		        INSERT INTO dbo.zsym_eventos (data_evento, id_registro, new_register_as_json, old_register_as_json, tabela, tipo, usuario)
		        VALUES (GETDATE(), @ID, @NewRecord, @OldRecord, ''Movimento'', @Operation, CURRENT_USER);

	   		END


	END;'

END;
-- TRIGGER Produto
IF  OBJECT_ID('AuditTrigger_Produto', 'TR') IS  NULL
BEGIN
EXEC dbo.sp_executesql @statement = N'CREATE TRIGGER dbo.AuditTrigger_Produto
	ON dbo.Produtos
	AFTER DELETE, INSERT, UPDATE
	AS
	BEGIN
	    SET NOCOUNT ON;
	   		BEGIN
				DECLARE @Operation NVARCHAR(10), @ID VARCHAR(20);

		        IF EXISTS (SELECT * FROM deleted)
		        BEGIN
		            IF EXISTS (SELECT * FROM inserted)
		                SET @Operation = ''UPDATE'';
		            ELSE
		                SET @Operation = ''DELETE'';
		        END
		        ELSE
		            SET @Operation = ''INSERT'';
				SELECT @ID = ISNULL((SELECT IdProduto FROM deleted), (SELECT IdProduto FROM inserted) )
		        DECLARE @OldRecord NVARCHAR(MAX) = (SELECT  *   FROM deleted FOR JSON AUTO, WITHOUT_ARRAY_WRAPPER);
		        DECLARE @NewRecord NVARCHAR(MAX) = (SELECT   *  FROM inserted FOR JSON AUTO, WITHOUT_ARRAY_WRAPPER);

		        INSERT INTO dbo.zsym_eventos (data_evento, id_registro, new_register_as_json, old_register_as_json, tabela, tipo, usuario)
		        VALUES (GETDATE(), @ID, @NewRecord, @OldRecord, ''Produtos'', @Operation, CURRENT_USER);

	   		END


	END;'

END;



