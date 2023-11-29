DECLARE
@IdPedidoPalm 	INT, @IdItemPedidoPalm INT, @OrigemSis  VARCHAR(20),
	       @StatusMovGerado VARCHAR(20), @TotLiqMovGer MONEY, @SituacaoPedido VARCHAR(1),    @SituacaoItemPedido 	VARCHAR(1),
	       @Qtd      		NUMERIC(15,6),  @QtdConfirmada NUMERIC(15,6),  @TotQtdConfirmada  NUMERIC(15,6),
	       @IdMovGerado     INT, @IdItemMovGerado INT,   @IdProduto  INT,
           @CODFILIAL	VARCHAR(2), @CODLOCAL  VARCHAR(2),   @CodCliFor   VARCHAR(7), @BloqueioFinanceiro  VARCHAR(1),
           @CodRetorno VARCHAR(2),   @DscRetorno   VARCHAR(50),  @CodRetornoItem VARCHAR(2),   @DscRetornoItem  VARCHAR(50)

SET @IdPedidoPalm = :PInt1

SET NOCOUNT ON

SELECT TOP 1
    @SituacaoPedido    = PedidoPalm.SituacaoPedido,
        @OrigemSis         = PedidoPalm.Origem,
       @IdMovGerado       = DADOS_MOV.IdMovGerado,
       @CodCliFor         = PedidoPalm.CodCliFor,
       @CodFilial         = Vendedores.CodFilial,
       @CodLocal          = Vendedores.CodLocal,
       @BloqueioFinanceiro= ISNULL(Dados_Mov.BloqueioFinanceiro,'N'),
       @StatusMovGerado   = ISNULL(Dados_Mov.StatusMov,''),
       @TotLiqMovGer      = ISNULL(Dados_Mov.TotLiqMovGer,0)

FROM dbo.PedidoPalm Pedidopalm LEFT JOIN dbo.CLI_FOR Cli_for ON Pedidopalm.CodCliFor = Cli_for.CODCLIFOR LEFT JOIN Cidade ON (Cli_for.IdCidade = Cidade.IdCidade) LEFT JOIN dbo.VENDEDORES Vendedores ON  PedidoPalm.CodVendedor = Vendedores.CodVendedor LEFT JOIN Filiais ON (Vendedores.CodFIlial = Filiais.CodFilial)  LEFT JOIN dbo.CONDPAG Condpag ON Pedidopalm.CodCondPag = Condpag.CODCONDPAG LEFT JOIN Portador ON PedidoPalm.CodPortador = Portador.CodPortador
    CROSS APPLY (SELECT NumMov=MAX(M.NumMov),  IdMovGerado =MAX(IdMov),  DtMovGerado = MAX(DtMov), DtEntSaiMovGerado = MAX(DataEntradaSaida), ChaveNFEMovGerado = MAX(ChaveNFe), SerieDocMovGerado = MAX(SerieDoc), CodCPMovGerado = MAX(m.CodCondPag), DtOperacaoMov = MAX(M.DataOperacao), StatusMov = MAX(dbo.fn_DscStatusMov(M.Status, TipoMov)), TipoMovGerado = MAX(dbo.fn_DscTipoMov2(TipoMov, DAV)), SituacaoMov = MAX(dbo.fn_DscStatusSep( StatusSeparacao)),
    BloqueioFinanceiro=MAX(M.BloqueioFinanceiro), TotProdMovGer=MAX(M.TOTMOV), BaseICMS=MAX(M.BaseICMS), ValorICMS=MAX(M.ValorICMS), BaseSubICMS=MAX(M.BaseSubICMS), ValorSubICMS=MAX(M.ValorSubICMS), ValorIPI=MAX(M.ValorIPI), ValorFrete=MAX(M.ValorFrete), ValorSeguro=MAX(M.ValorSeguro),
    OutrasDespesas=MAX(M.OutrasDespesas), TotLiqMovGer = MAX(dbo.fn_ValorMovimento2( M.IdMov, M.TotMov, M.PercDesconto, M.ValorSubICMS, M.ValorFrete, M.ValorSeguro, M.OutrasDespesas, M.ValorIPI, M.Frete,'L'))
    FROM Movimento M WHERE  M.IdPedidoPalm = PedidoPalm.IdPedidoPalm) AS DADOS_MOV
WHERE PedidoPalm.IdPedidoPalm      = @IdPedidoPalm


    IF @OrigemSis = 'CLOSEUP'  -- Também grava Cod.Retorno nos PENDENTES
BEGIN

 DECLARE xItens SCROLL CURSOR FOR
SELECT IdItemPedidoPalm,  SituacaoItemPedido, Itempedidopalm.IdProduto, ISNULL(Qtd,0), ISNULL(QtdConfirmada,0), Dados_im.IdItemMovGerado
FROM Itempedidopalm CROSS APPLY (SELECT
	IdItemMovGerado  = MAX(im.IdItemMov),   UnidItemMov = MAX(im.UnidItemMov),   PercICMS=MAX(im.PercICMS), PercIPI=MAX(im.PercIPI), CFOP = MAX(NatOP.CFPO), SitTributariaItem=MAX(im.SitTributariaItem),
	ValorItemICMS = CONVERT(NUMERIC(12,2) , MAX( dbo.fn_ValorItemMov4('VI', im.IdMov, im.IdItemMov, im.IdProduto, im.PrecoUnit, im.PercDescontoItem, im.Qtd, im.PercICMS, im.PercIPI, im.PercISS, im.MVAitem, TipoMov, Movimento.CodCliFor, Movimento.CodFilial, CodFilialDest, CodLocalDest, Movimento.PercDesconto, CalculaICMSsubstituto,  DescNaoIncideICMS, im.IdNaturezaOperacao, SitTributariaItem,ValorIPIincideICMS,ValorFreteincideICMS,PercReducaoBICMS,PercSubICMS, ValorItemFrete, ValorItemOutDespesas ))),
	BaseItemICMS = CONVERT(NUMERIC(12,2) ,  MAX( dbo.fn_ValorItemMov4('BI', im.IdMov, im.IdItemMov, im.IdProduto, im.PrecoUnit, im.PercDescontoItem, im.Qtd, im.PercICMS, im.PercIPI, im.PercISS, im.MVAitem, TipoMov, Movimento.CodCliFor, Movimento.CodFilial, CodFilialDest, CodLocalDest, Movimento.PercDesconto, CalculaICMSsubstituto,  DescNaoIncideICMS, im.IdNaturezaOperacao, SitTributariaItem,ValorIPIincideICMS,ValorFreteincideICMS,PercReducaoBICMS,PercSubICMS, ValorItemFrete, ValorItemOutDespesas ))),
	BaseItemSubICMS = 0.00,
	ValorItemIPI = 0.00
 FROM ItensMov im, NaturezaOperacao NatOP, PedidoPalm, Movimento  WHERE Itempedidopalm.IdPedidoPalm = PedidoPalm.IdPedidoPalm AND im.IdMov = Movimento.IdMov AND PedidoPalm.IdPedidoPalm = Movimento.IdPedidoPalm AND im.IdProduto = Itempedidopalm.IdProduto AND im.IdNaturezaOperacao = NatOP.IdNaturezaOperacao) AS DADOS_IM
 LEFT JOIN Produtos P ON ( Itempedidopalm.IdProduto = P.IdProduto) LEFT JOIN ClassePIS ON (P.IdClassePIS = ClassePIS.IdClassePIS)

WHERE (Itempedidopalm.IdPedidoPalm = @IdPedidoPalm)

    FOR READ ONLY

    OPEN xItens
    FETCH FIRST FROM xItens
INTO @IdItemPedidoPalm,
    @SituacaoItemPedido,
    @IdProduto,
    @Qtd,
    @QtdConfirmada,
    @IdItemMovGerado

SET @CodRetorno = ''
SET @DscRetorno = ''
SET @CodRetornoItem = ''
SET @DscRetornoitem = ''
SET @TotQtdConfirmada = 0



    IF ISNULL(@CodCliFor,'') = ''
BEGIN
 SET @CodRetorno = '02'
 SET @DscRetorno = 'Cliente não cadastrado'
END
ELSE IF ISNULL(@CodCliFor,'') <> '' AND @BloqueioFinanceiro = 'S'
BEGIN
 SET @CodRetorno = '04'
 SET @DscRetorno = 'FALTA DE LIMITE DE CRÉDITO'
END
ELSE IF ISNULL(@CodCliFor,'') <> '' AND @StatusMovGerado = 'Cancelado'
BEGIN
 SET @CodRetorno = '11'
 SET @DscRetorno = 'PEDIDO NÃO PROCESSADO'
END
ELSE IF ISNULL(@CodCliFor,'') <> '' AND @StatusMovGerado <> 'Cancelado' AND @TotLiqMovGer <= 30
BEGIN
 SET @CodRetorno = '05'
 SET @DscRetorno = 'PEDIDO NÃO ALCANÇOU O VALOR MINIMO'
END


SET @CodRetornoItem = @CodRetorno
SET @DscRetornoitem = @DscRetorno

UPDATE ItemPedidoPalm SET CodRetornoItem = @CodRetornoItem, DscRetornoItem = CONVERT(VARCHAR(30),@DscRetornoitem) WHERE IdPedidoPalm = @IdPedidoPalm

    WHILE @@FETCH_STATUS = 0 AND @CodRetorno NOT IN ('02','04','11','05')
BEGIN

  IF @SituacaoItemPedido IN ('P')
BEGIN

     IF @IdProduto = 0
BEGIN
        SET @CodRetornoItem = '06';
        SET @DscRetornoitem = 'PRODUTO NÃO CADASTRADO/DESATIVADO'
END

END
ELSE IF @SituacaoItemPedido IN ('C','I') -- C-Confirmado   P-Pendente   I-Inconsistente   X-Cancelado   D-Inadimplência
BEGIN


   if (@Qtd > @QtdConfirmada)  AND (@QtdConfirmada > 0)
begin
        SET @CodRetornoItem = '09';
        SET @DscRetornoitem = 'PRODUTO ATENDIDO PARCIALMENTE'
end
else if (@Qtd = @QtdConfirmada)  AND (@QtdConfirmada > 0)
begin
        SET @CodRetornoItem = '00';
        SET @DscRetornoitem = 'PRODUTO FATURADO';
end
else if (@Qtd > @QtdConfirmada)  AND (@QtdConfirmada = 0)
begin
        SET @CodRetornoItem = '08';
        SET @DscRetornoitem = 'FALTA NO ESTOQUE';
end

	SET @TotQtdConfirmada = @TotQtdConfirmada + @QtdConfirmada
END


UPDATE ItemPedidoPalm SET CodRetornoItem = @CodRetornoItem, DscRetornoItem = CONVERT(VARCHAR(30),@DscRetornoitem)
WHERE IdItemPedidoPalm = @IdItemPedidoPalm

    FETCH NEXT FROM xItens
INTO @IdItemPedidoPalm,
    @SituacaoItemPedido,
    @IdProduto,
    @Qtd,
    @QtdConfirmada,
    @IdItemMovGerado

END

CLOSE xItens
    DEALLOCATE xItens


IF @CodRetorno = ''  -- Caso o Pedido tenha sido atendimento parcialmente
 IF @TotQtdConfirmada > 0
BEGIN
  SET @CodRetorno = '09'
  SET @DscRetorno = 'PEDIDO CONCLUÍDO COM SUCESSO'
END


UPDATE PedidoPalm SET CodRetorno = @CodRetorno, DscRetorno = CONVERT(VARCHAR(30),@DscRetorno)
WHERE IdPedidoPalm = @IdPedidoPalm


END  -- IF ORIGEM
SELECT OK = 'N'

