-- Movimento que nao veio da petronas
-- REGRA Para AVISAR ao integrador se o pedido deve ser enviado para a PETRONAS ou nao
-- Suporte CRONOS por favor nao desativar sem antes falar com Lucas (84) 99850-6034
IF :Pchar1 IN ('2.4')
    BEGIN
        IF EXISTS (SELECT 1
                   FROM ItensMov im  WITH (NOLOCK)
                            INNER JOIN Produtos p WITH (NOLOCK) ON im.IdProduto = p.IdProduto
                            INNER JOIN Fabricantes f ON p.CodFabr = f.CodFabr
                            INNER JOIN Movimento m ON m.IdMov = im.IdMov
                   WHERE im.IdMov = :IdMov AND f.NOMEFABR = 'PETRONAS' AND ISNULL(m.IdPedidoPalm, 0) = 0 )
            BEGIN
                IF EXISTS (SELECT 1 FROM ZMovimentoCompl zc WHERE IdMov = :IdMov)
                    BEGIN
                        UPDATE ZMovimentoCompl set sym_enviar_petronas = 1 WHERE IdMov = :IdMov
                    END
                ELSE
                    BEGIN
                        INSERT INTO ZMovimentoCompl (IdMov, sym_enviar_petronas) VALUES (:IdMov, 1)
                    END
            END
    END
SELECT OK = 'N'

-- Clientes
-- REGRA Para AVISAR ao integrador se o registro deve ser enviado para a PETRONAS ou nao
-- Suporte CRONOS por favor nao desativar sem antes falar com Lucas (84) 99850-6034

IF :Pchar1 LIKE 'C%'
    BEGIN
        IF EXISTS (SELECT 1 FROM Cli_For cf WHERE CodCliFor = :Pchar1 AND ISNULL(Inativo,'N') = 'N')
            BEGIN
                IF EXISTS (SELECT 1 FROM ZCli_ForCompl zfc WHERE CodCliFor = :Pchar1)
                    BEGIN
                        UPDATE ZCli_ForCompl set sym_enviar_petronas = 1 WHERE IdMov = :IdMov
                    END
                ELSE
                    BEGIN
                        INSERT INTO ZCli_ForCompl (CodCliFor, sym_enviar_petronas) VALUES (:Pchar1, 1)
                    END
            END
    END

SELECT OK = 'N'

-- PRODUTOS
-- REGRA Para AVISAR ao integrador se o registro deve ser enviado para a PETRONAS ou nao
-- Suporte CRONOS por favor nao desativar sem antes falar com Lucas (84) 99850-6034

BEGIN
    IF EXISTS (SELECT 1 FROM Produtos p WHERE p.IdProduto = :PINT1 AND ISNULL(Inativo,'N') = 'N')
        BEGIN
            IF EXISTS (SELECT 1 FROM ZProdutosCompl zc WHERE IdProduto = :PINT1)
                BEGIN
                    UPDATE ZProdutosCompl set sym_enviar_petronas = 1 WHERE IdProduto = :PINT1
                END
            ELSE
                BEGIN
                    INSERT INTO ZProdutosCompl (IdProduto, sym_enviar_petronas) VALUES (:PINT1, 1)
                END
        END
END

SELECT OK = 'N'

-- NOVO CPR
-- REGRA Para AVISAR ao integrador se o registro deve ser enviado para a PETRONAS ou nao
-- Suporte CRONOS por favor nao desativar sem antes falar com Lucas (84) 99850-6034

IF :Pchar1 IN ('2.4')
    BEGIN
        IF EXISTS (SELECT 1
                   FROM CPR WITH (NOLOCK)
                            INNER JOIN Movimento m WITH (NOLOCK) ON m.IdMov  = CPR.IdMov
                   WHERE m.IdMov = :IdMov)
            BEGIN
                DECLARE @IDCPR INT
                DECLARE XCURSOR CURSOR FOR
                    SELECT IdCPR FROM CPR c WHERE c.IdMov = :IdMov
                OPEN XCURSOR
                FETCH NEXT FROM XCURSOR INTO @IDCPR
                WHILE @@FETCH_STATUS = 0
                    BEGIN
                        IF EXISTS (SELECT 1 FROM ZCPRCompl WHERE IdCPR = @IDCPR)
                            BEGIN
                                UPDATE ZCPRCompl set sym_enviar_petronas = 1 WHERE IdCPR = @IDCPR
                            END
                        ELSE
                            BEGIN
                                INSERT INTO ZCPRCompl (IdCPR, sym_enviar_petronas) VALUES (@IDCPR, 1)
                            END

                        FETCH NEXT FROM XCURSOR INTO @IDCPR
                    END

                CLOSE XCURSOR
                DEALLOCATE XCURSOR
            END
    END

SELECT OK = 'N'

-- CPR Existente
-- REGRA Para AVISAR ao integrador se o registro deve ser enviado para a PETRONAS ou nao
-- Suporte CRONOS por favor nao desativar sem antes falar com Lucas (84) 99850-6034
IF EXISTS (SELECT 1
           FROM CPR WITH (NOLOCK)
                    INNER JOIN Movimento m WITH (NOLOCK) ON m.IdMov = CPR.IdMov
           WHERE CPR.IdCPR = :PInt1 AND m.TipoMov IN ('2.4', '2.3', '2.8'))
    BEGIN
        BEGIN
            IF EXISTS (SELECT 1 FROM ZCPRCompl WHERE IdCPR = :PInt1)
                BEGIN
                    UPDATE ZCPRCompl set sym_enviar_petronas = 1 WHERE IdCPR = :PInt1
                END
            ELSE
                BEGIN
                    INSERT INTO ZCPRCompl(IdCPR, sym_enviar_petronas) VALUES (:PInt1, 1)
                END
        END
    END

SELECT OK = 'N'


-- Procedure para transformar pedidoPalm em movimento

CREATE PROCEDURE sym_converte_pedido
    @IdPedidoPalm INT,
    @TipoMov VARCHAR(3),
    @CodLocal VARCHAR(2)

AS
    SET NOCOUNT ON
DECLARE  @IdItemPedidoPalm INT, @PrecoUnit MONEY, @Qtd NUMERIC(15,6),  @QtdSolic NUMERIC(15,6), @SdoAtual NUMERIC(15,6),
@IdMovNew  INT, @Item  INT, @IdItemMovNew  INT,  @NumeroMovNew VARCHAR(20), @AceitaEstoqueNegativo VARCHAR(1),  @ControleLote  VARCHAR(1),
@IdProduto INT, @CodProduto VARCHAR(20), @IDMOV INT, @CODFILIAL VARCHAR(2), @IDEMPRESA INT, @INDPRECOVENDA VARCHAR(1),
@SITUACAOITEMPEDIDO VARCHAR(1), @UnidItemMov  VARCHAR(4), @PercComissaoItem NUMERIC(6,2), @PercDescontoItem FLOAT
    SET @IDEMPRESA = 1
    SET @IdMovNew = 0
    IF NOT EXISTS( SELECT 1 FROM Movimento M (NOLOCK) WHERE TipoMov =@TipoMov AND IdPedidoPalm = @IdPedidoPalm AND Status <> 'C')
        BEGIN
            SELECT @NumeroMovNew = sym_nextNumMov FROM ZFiliaisCompl WHERE CodFilial = '01'
            UPDATE ZFiliaisCompl SET sym_nextNumMov = CAST(sym_nextNumMov as BigInt) + 1

            EXEC @IdMovNew = dbo.sp_NextId 'Movimento'
            -- Inserindo Movimento
            BEGIN TRANSACTION
                INSERT INTO MOVIMENTO (IDEMPRESA, CODFILIAL, CODLOCAL, IDMOV, TIPOMOV, NUMMOV, DTMOV, CODCLIFOR, CODCONDPAG, CODVENDEDOR, IdRegiao, PERCCOMISSAO, STATUS, PercDesconto, Observacoes, DataEntrega, DataOperacao, IdUsuario, IdPedidoPalm, IdExpedicao, NumMovAux )
                SELECT @IdEmpresa,
                       PedidoPalm.CodFilial,
                       @CodLocal,
                       @IdMovNew,
                       @TipoMov,
                       CONVERT(VARCHAR(20), @NumeroMovNew),
                       PedidoPalm.DataPedido,
                       PedidoPalm.CODCLIFOR,
                       PedidoPalm.CODCONDPAG,
                       PedidoPalm.CODVENDEDOR,
                       Cli_For.IdRegiao,
                       Vendedores.ComissaoVendedor,
                       'T',
                       ISNULL(PedidoPalm.PercDesconto,0),
                       CONVERT(VARCHAR(400),'Portador: '+ISNULL(Portador.NomePortador,'')+'  /  '+'Obs: '+ISNULL(PedidoPalm.Observacoes,'')),
                       PedidoPalm.DataEntrega,
                       GETDATE(),
                       PedidoPalm.Origem,
                       PedidoPalm.IdPedidoPalm,
                       PedidoPalm.IdExpedicao,
                       PedidoPalm.NumNF
                FROM PedidoPalm LEFT JOIN Portador ON PedidoPalm.CodPortador = Portador.CodPortador
                                LEFT JOIN Cli_For ON PedidoPalm.CodCliFor = Cli_For.CodCliFor
                                LEFT JOIN Vendedores ON PedidoPalm.CodVendedor = Vendedores.CodVendedor
                WHERE PedidoPalm.IdPedidoPalm   = @IdPedidoPalm
                -- Inserindo Itens
                DECLARE xItens SCROLL CURSOR FOR
                    SELECT PedidoPalm.CodFilial, @CodLocal, IdItemPedidoPalm, Item, CodProduto, Qtd, IdPrecoTabela, PrecoUnit, ISNULL(PercDescontoItem,0)
                    FROM ItemPedidoPalm INNER JOIN PedidoPalm ON ItemPedidoPalm.IdPedidoPalm = PedidoPalm.IdPedidoPalm
                    WHERE PedidoPalm.IdPedidoPalm  = @IdPedidoPalm

                    FOR READ ONLY

                OPEN xItens
                FETCH FIRST FROM xItens
                    INTO @CodFilial, @CodLocal,
                        @IdItemPedidoPalm,
                        @Item,
                        @CodProduto,
                        @Qtd,
                        @IndPrecoVenda,
                        @PrecoUnit,
                        @PercDescontoItem

                WHILE @@FETCH_STATUS = 0
                    BEGIN

                        SELECT @SituacaoItemPedido = 'C'

                        SELECT @IdProduto = MAX(IdProduto),
                               @PercComissaoItem = ISNULL(MAX(ComissaoProduto),0),
                               @UnidItemMov  = MAX(Produtos.Unid),
                               @ControleLote = ISNULL(MAX(Produtos.ControleLote),'N')
                        FROM Produtos
                        WHERE CodProduto = @CodProduto

                        -- Produto nao cadastrado +
                        IF @IdProduto IS NULL
                            SELECT @SituacaoItemPedido = 'I'

                        SELECT @SdoAtual   = ISNULL(MAX(e.SdoAtual),0)
                        FROM Estoque e
                        WHERE e.Codfilial = @CodFilial
                          AND e.CodLocal  = @CodLocal
                          AND e.IdProduto = @IdProduto

                        SELECT @AceitaEstoqueNegativo = ISNULL(MAX(Loc.AceitaEstoqueNegativo),'N')
                        FROM LocalEstoque Loc
                        WHERE Loc.IdEmpresa = @IdEmpresa
                          AND Loc.CodFilial = @CodFilial
                          AND Loc.CodLocal  = @CodLocal

                        SET @QtdSolic = @Qtd

                        IF @AceitaEstoqueNegativo = 'N'
                            IF (@Qtd > @SdoAtual) AND @TipoMov = '2.1'
                                BEGIN
                                    IF @SdoAtual > 0
                                        SELECT @Qtd = @SdoAtual
                                    ELSE
                                        SELECT @Qtd = 0
                                    SELECT @SituacaoItemPedido = 'I'
                                END

                        IF @IdProduto IS NOT NULL AND NOT EXISTS (SELECT 1 FROM ItensMov im (NOLOCK) WHERE im.IdMov = @IdMovNew AND im.IdProduto = @IdProduto)
                            BEGIN

                                EXEC @IdItemMovNew = dbo.sp_NextId 'ItensMov'

                                INSERT INTO ItensMov (IdEmpresa, IdItemMov, IDMOV, IdProduto, UnidItemMov,  QtdSolic, QTD, PRECOUNIT, IDPRECOTABELA, PercDescontoItem, PercComissaoItem, DATAOPERACAO, IDUSUARIO)
                                VALUES(@IdEmpresa,
                                       @IdItemMovNew,
                                       @IdMovNew,
                                       @IdProduto,
                                       @UnidItemMov,
                                       @QtdSolic,
                                       dbo.fn_Fmt(@QTD,'Q'),
                                       dbo.fn_Fmt(@PRECOUNIT,'P'),
                                       @IndPrecoVenda,
                                       @PercDescontoItem,
                                       @PercComissaoItem,
                                       GETDATE(),
                                       'PALM'
                                      )

                                IF @ControleLote = 'S'
                                    EXEC dbo.sp_GeraItemLoteAuto @IdItemMovNew

                            END

                        UPDATE dbo.ItemPedidoPalm SET SituacaoItemPedido = @SituacaoItemPedido, QtdConfirmada = @Qtd
                        WHERE IdItemPedidoPalm  = @IdItemPedidoPalm


                        FETCH NEXT FROM xItens
                            INTO @CodFilial, @CodLocal,
                                @IdItemPedidoPalm,
                                @Item,
                                @CodProduto,
                                @Qtd,
                                @IndPrecoVenda,
                                @PrecoUnit,
                                @PercDescontoItem

                    END

                CLOSE xItens
                DEALLOCATE xItens

                -- Grava o pedido como C-Confirmado
                IF EXISTS (SELECT 1 FROM Movimento WHERE IdMov = @IdMovNew)
                    UPDATE PedidoPalm SET SituacaoPedido = 'C', NumPedidoCRONOS = @NumeroMovNew
                    WHERE IdPedidoPalm  = @IdPedidoPalm
                ELSE
                    SET @IdMovNew = 0
                SELECT VALOR = @NumeroMovNew
        END


    IF @@ERROR = 0
        COMMIT
    ELSE
        ROLLBACK
    RAISERROR('Nao foi possivel inserir os dados',1,1)