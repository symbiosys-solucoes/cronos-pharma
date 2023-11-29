
-- Query Insercao PedidoPalm 

BEGIN TRANSACTION 

 INSERT INTO  PedidoPalm ( Origem, IdEmpresa, NumPedidoPalm, CodVendedor, CodCliFor, DataPedido, CodCondPag, CodPortador, TotalPedido, SituacaoPedido, IdUsuario, DataOperacao, CnpjCpfCliFor, CodFilial)
              
	OUTPUT INSERTED.*
		SELECT 	
		ORIGEM = :origem ,
		IDEMPRESA = :idEmpresa,
		NUMPEDIDOPALM = :numPedidoPalm, 
		CODVENDEDOR = ISNULL(:codVendedor,'00001'),
		CODCLIFOR = :codCliFor,
		DATAPEDIDO = :dataPedido,
		CODCONDPAG = ISNULL(:codCondPag,'01'),
		CODPORTADOR = ISNULL(:codPortador,'01'),
		TOTAL = :valorTotal,
		SITUACAO = :situacao,
		IDUSUARIO = :idUsuario,
		DATAOPERACAO = :dataOperacao,
		CNPJ = :cnpj,
		FILIAL = :codFilial

IF @@ERROR = 0
COMMIT
ELSE
ROLLBACK
RAISERROR('Nao foi possivel inserir os dados',1,1)		