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
