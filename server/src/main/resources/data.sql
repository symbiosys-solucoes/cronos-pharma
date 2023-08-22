IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'ZFiliaisCompl') AND type in (N'U'))
BEGIN
CREATE TABLE ZFiliaisCompl (
                               CodFilial varchar(2) COLLATE SQL_Latin1_General_CP1_CI_AI NOT NULL,
                               CONSTRAINT PK_ZFiliaisCompl PRIMARY KEY (CodFilial))
END

IF NOT EXISTS (SELECT 1 FROM ConfigCompl WHERE Tabela = 'ZFiliaisCompl' AND NomeColuna = 'sym_nextNumMov' )
BEGIN
INSERT INTO ConfigCompl (NomeColuna, Tabela, Descricao, Tipo, Tamanho, Inativo, DataOperacao, IdUsuario, SoLeitura) VALUES
    ('sym_nextNumMov', 'ZFiliaisCompl', 'Numero Pedido', 'A', 15, 'N', GETDATE(), 'SYM', 'S')
ALTER TABLE ZFiliaisCompl ADD sym_nextNumMov VARCHAR(15)
END

IF NOT EXISTS (SELECT 1 FROM ConfigCompl WHERE Tabela = 'ZFiliaisCompl' AND NomeColuna = 'sym_parametros' )
BEGIN
INSERT INTO ConfigCompl (NomeColuna, Tabela, Descricao, Tipo, Tamanho, Inativo, DataOperacao, IdUsuario, SoLeitura) VALUES
    ('sym_parametros', 'ZFiliaisCompl', 'Numero Pedido', 'M', 800, 'N', GETDATE(), 'SYM', 'S')
ALTER TABLE ZFiliaisCompl ADD sym_parametros TEXT
END

IF (SELECT sym_nextNumMov FROM ZFiliaisCompl zc WHERE CodFilial = '01') IS NULL
BEGIN
UPDATE ZFiliaisCompl SET sym_nextNumMov = '5000000000' WHERE CODFILIAL = '01'
END


BEGIN
  DECLARE @FILIAL VARCHAR(2)
  DECLARE FILIAL CURSOR FOR
SELECT f.CODFILIAL FROM Filiais f LEFT JOIN ZFiliaisCompl zc ON f.CodFilial = zc.CodFilial WHERE zc.sym_parametros IS NULL
    OPEN FILIAL
	 FETCH NEXT FROM FILIAL INTO @FILIAL
    WHILE @@FETCH_STATUS = 0
BEGIN
		IF NOT EXISTS (SELECT 1 FROM ZFiliaisCompl zc2 where CodFilial = @FILIAL)
BEGIN
INSERT INTO ZFiliaisCompl (CodFilial, sym_parametros) VALUES (@FILIAL, '{"codigoFilial":"'+@FILIAL+'","codigoDistribuidorPetronas":"00", "active":false}')
END
ELSE
BEGIN
UPDATE ZFiliaisCompl set sym_parametros = '{"codigoFilial":"'+@FILIAL+'","codigoDistribuidorPetronas":"00", "active":false}' WHERE CodFilial = @FILIAL
END
FETCH NEXT FROM FILIAL INTO @FILIAL
END
CLOSE FILIAL
    DEALLOCATE FILIAL
END





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
)
END

IF NOT EXISTS (
    SELECT *
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'ZProdutosCompl'
    AND COLUMN_NAME = 'sym_ativo'
)
BEGIN
ALTER TABLE ZProdutosCompl
    ADD sym_ativo VARCHAR(1)

END
IF NOT EXISTS (
    SELECT *
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'ZProdutosCompl'
    AND COLUMN_NAME = 'sym_usaFTP'
)
BEGIN
ALTER TABLE ZProdutosCompl
    ADD sym_usaFTP VARCHAR(1)

END
IF NOT EXISTS (
    SELECT *
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'ZProdutosCompl'
    AND COLUMN_NAME = 'sym_url'
)
BEGIN
ALTER TABLE ZProdutosCompl
    ADD sym_url VARCHAR(255)

END
IF NOT EXISTS (
    SELECT *
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'ZProdutosCompl'
    AND COLUMN_NAME = 'sym_login'
)
BEGIN
ALTER TABLE ZProdutosCompl
    ADD sym_login VARCHAR(255)

END
IF NOT EXISTS (
    SELECT *
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'ZProdutosCompl'
    AND COLUMN_NAME = 'sym_senha'
)
BEGIN
ALTER TABLE ZProdutosCompl
    ADD sym_senha VARCHAR(255)

END
IF NOT EXISTS (
    SELECT *
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'ZProdutosCompl'
    AND COLUMN_NAME = 'sym_dir_ped_ftp'
)
BEGIN
ALTER TABLE ZProdutosCompl
    ADD sym_dir_ped_ftp VARCHAR(255)

END
IF NOT EXISTS (
    SELECT *
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'ZProdutosCompl'
    AND COLUMN_NAME = 'sym_dir_ret_ftp'
)
BEGIN
ALTER TABLE ZProdutosCompl
    ADD sym_dir_ret_ftp VARCHAR(255)

END
IF NOT EXISTS (
    SELECT *
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'ZProdutosCompl'
    AND COLUMN_NAME = 'sym_dir_ped_local'
)
BEGIN
ALTER TABLE ZProdutosCompl
    ADD sym_dir_ped_local VARCHAR(255)

END
IF NOT EXISTS (
    SELECT *
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'ZProdutosCompl'
    AND COLUMN_NAME = 'sym_dir_ret_local'
)
BEGIN
ALTER TABLE ZProdutosCompl
    ADD sym_dir_ret_local VARCHAR(255)

END
IF NOT EXISTS (
    SELECT *
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'ZProdutosCompl'
    AND COLUMN_NAME = 'sym_dir_imp_local'
)
BEGIN
ALTER TABLE ZProdutosCompl
    ADD sym_dir_imp_local VARCHAR(255)

END
IF NOT EXISTS (
    SELECT *
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'ZProdutosCompl'
    AND COLUMN_NAME = 'CodTd_sym_tipo'
)
BEGIN
ALTER TABLE ZProdutosCompl
    ADD CodTd_sym_tipo VARCHAR(255)

END
IF NOT EXISTS (
    SELECT *
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'ZProdutosCompl'
    AND COLUMN_NAME = 'sym_dir_est_ftp'
)
BEGIN
ALTER TABLE ZProdutosCompl
    ADD sym_dir_est_ftp VARCHAR(255)

END
IF NOT EXISTS (
    SELECT *
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'ZProdutosCompl'
    AND COLUMN_NAME = 'sym_dir_est_local'
)
BEGIN
ALTER TABLE ZProdutosCompl
    ADD sym_dir_est_local VARCHAR(255)

END
IF NOT EXISTS (
    SELECT *
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'ZProdutosCompl'
    AND COLUMN_NAME = 'sym_dir_preco_ftp'
)
BEGIN
ALTER TABLE ZProdutosCompl
    ADD sym_dir_preco_ftp VARCHAR(255)

END
IF NOT EXISTS (
    SELECT *
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'ZProdutosCompl'
    AND COLUMN_NAME = 'sym_dir_preco_local'
)
BEGIN
ALTER TABLE ZProdutosCompl
    ADD sym_dir_preco_local VARCHAR(255)

END




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
	    FoneCliFor,
	    FaxCliFor,
	    CelCliFor,
	    DtNascCli,
	    Sexo,
	    ListaNegra,
	    LimiteCredito,
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
	    DataInicioAtividades,
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
	    PosLatitudeCad,
	    PosLongitudeCad,
	    Cnae,
	    ProdutorRural,
	    CodSUFRAMA,
	    DataOperacao,
	    IdUsuario,
	    WebSite FROM deleted FOR JSON AUTO, WITHOUT_ARRAY_WRAPPER);
	        DECLARE @NewRecord NVARCHAR(MAX) = (SELECT     CodCliFor,
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
	    FoneCliFor,
	    FaxCliFor,
	    CelCliFor,
	    DtNascCli,
	    Sexo,
	    ListaNegra,
	    LimiteCredito,
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
	    DataInicioAtividades,
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
	    PosLatitudeCad,
	    PosLongitudeCad,
	    Cnae,
	    ProdutorRural,
	    CodSUFRAMA,
	    DataOperacao,
	    IdUsuario,
	    WebSite FROM inserted FOR JSON AUTO, WITHOUT_ARRAY_WRAPPER);
	
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

IF  OBJECT_ID('AuditTrigger_Movimento', 'TR') IS NOT NULL
BEGIN
    DROP TRIGGER dbo.AuditTrigger_Movimento
END
ELSE
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
		        DECLARE @NewRecord NVARCHAR(MAX) = (SELECT   *,
                    Portador = ISNULL((SELECT MAX(Portador.NomePortador) FROM CPR INNER JOIN Portador ON CPR.Codportador = Portador.CodPortador WHERE IdMov = inserted.IdMov), ''BOLETO''),
                    CondPag = ISNULL((SELECT CondPag FROM CondPag WHERE CodCondPag = inserted.CodCondPag),''DINHEIRO'')  FROM inserted
                    FOR JSON AUTO, WITHOUT_ARRAY_WRAPPER);

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



