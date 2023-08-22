
## Campos complementares

Criar os seguintes campos complementares, no módulo gestor, do sistema Cronos.

### Tabelas Dinâmicas

* Sigla: sym_tipo : Fica os tipos de integrações

### Produtos
****
* **sym_usaFTP : Checkbox** (Informa se esse produto utiliza conexão via FTP)
* **sym_ativo :  Checkbox** (Informa se esse produto deve ser analisado pelo sistema)
* **sym_url : Varchar(255)** (url de ftp no padrao: symbiosyssolucoes.com.br)
* **sym_login : Varchar(255)** (login do usuário)
* **sym_senha : Varchar(255)** (Senha do usuário)
* **sym_dir_ped_ftp : Varchar(255)** (Diretório que fica salvo os pedidos no FTP)
* **sym_dir_ret_ftp : Varchar(255)** (Diretório que será enviado retornos no FTP)
* **sym_dir_ped_local : Varchar(255)** (Diretório aonde o sistema vai baixar os pedidos)
* **sym_dir_imp_local : Varchar(255)** (Diretório, aonde será enviado os pedidos após importados)
* **sym_dir_ret_local : Varchar(255)** (Diretório, aonde ficará os retornos gerados pelo sistema)
* **sym_dir_est_local : Varchar(255)** (Diretório, aonde o sistema irá gerar os arquivos de estoque)
* **sym_dir_est_ftp : Varchar(255)** (Diretório, aonde o sistema irá enviar os arquivos de estoque no FTP)
* **CodTd_sym_tipo: Tabela Dinâmica(sym_tipo)**
  * Tipos Configurados:
    * **EMS**
    * **CONSYS**

### Filiais

* **sym_nextNumMov: Varchar(15)** (Preferencialmente colocar o valor _5000000000_ de início)    

### Movimento

* **sym_deveFinalizar : Checkbox** (Quando uma regra de negócio, do tipo abortar é acionada, ele marca esse campo, para que os devidos erros sejam corrigidos)

## Instalação

1. Criar pasta como o nome Symbiosys no disco local C:\ exemplo "C:\Symbiosys\"
2. Extrair o conteúdo do arquivo zip na pasta "C:\Symbiosys\".
3. Baixar a jdk 11, preferencialmente a versão correto da Amazon, 
   * x64: clique [aqui](https://corretto.aws/downloads/latest/amazon-corretto-11-x64-windows-jdk.zip)
   * x86: clique [aqui](https://corretto.aws/downloads/latest/amazon-corretto-11-x86-windows-jdk.zip)
4. Extrair o java na pasta "C:\Symbiosys\" o caminho da pasta do java deve ser "C:\Symbiosys\jdk11\"
5. Configurar o Arquivo application.properties com as seguintes definições:
   1. spring.datasource.url=jdbc:sqlserver://<endereco do servidor>;databaseName=<nome do banco de dados> _Exemplo: spring.datasource.url=jdbc:sqlserver://127.0.0.1;databaseName=dbCronos_
   2. spring.datasource.username=<usuario do banco de dados> _Exemplo: spring.datasource.username=master_ 
   3. spring.datasource.password=<senha do banco de dados> _Exemplo: spring.datasource.password=master_
   4. app.filial.cnpj=<filial para geração de arquivos>
   5. app.filial.local.estoque=<código do local de estoque>
   6. app.filial=01
   7. app.cron.busca.ftp= 0 0/10 * * * *
   8. app.cron.gera.estoque= 0 * * * * *


# Petronas
codigos de integracao(dtcode) da faal FAALRN
FAALPB