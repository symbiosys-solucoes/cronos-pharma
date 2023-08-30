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





