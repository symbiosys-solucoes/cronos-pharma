-- Query Insercao ItemPedidoPalm

BEGIN TRANSACTION

	INSERT INTO ITEMPEDIDOPALM (IDPEDIDOPALM,ITEM,IDEMPRESA,IDPRODUTO,CODPRODUTO,QTD,QtdConfirmada,IDPRECOTABELA,PRECOUNIT,PERCDESCONTOITEM,PercComissaoItem,SituacaoItemPedido,LogImportacao,IdUsuario,DataOperacao,CodRetornoItem,DscRetornoItem,CodProdutoArq)
              
	OUTPUT INSERTED.*
     
		SELECT 
		IDPEDIDOPALM = :idPedidoPalm,
		ITEM = :item,
		IDEMPRESA = :idEmpresa,
		IDPRODUTO = :idProduto,
		CODPRODUTO = :codProduto,
		QTD = :qtd,
		QTDCONFIRMADA = :qtdConfirmada,
		IDPRECOTABELA = :idPrecoTabela,
		PRECOUNIT = :idPrecoUnit,
		PERCDESCONTOITEM = :percDesc,
		PERCCOMISSAOITEM = :percComissao,
		SITUACAOITEMPEDIDO = :situacaoItem,
		LOGIMPORTACAO = :logImportacao,
		IDUSUARIO = :idUsuario,
		DATAOPERACAO = :dataOperacao,
		CODRETORNOITEM = :codRetorno,
		DSCRETORNOITEM = :dscRetorno,
		CODPRODUTOARQ = :codProdutoArq

IF @@ERROR = 0
COMMIT
ELSE
ROLLBACK
RAISERROR('Nao foi possivel inserir os dados',1,1)		
