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
INSERT INTO ZProdutosCompl
SELECT 'S', 'N', '127.0.0.1', 'user', 'pws', '/ped/', '/ret/', 'C:\CRONOS\SYM\CLOSEUP\PEDIDO\', 'C:\CRONOS\SYM\CLOSEUP\RETORNO\',
       'C:\CRONOS\SYM\CLOSEUP\PEDIDO\IMPORTADOS\', 'CLOSEUP', '/estoque/', 'C:\CRONOS\SYM\CLOSEUP\ESTOQUE\','/preco/', 'C:\CRONOS\SYM\CLOSEUP\PRECO\', 1;
--FROM ZProdutosCompl