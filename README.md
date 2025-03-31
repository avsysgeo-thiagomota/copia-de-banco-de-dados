# Projeto: Prova de conceito de Copia de Tabelas e Relacionamentos entre Bancos de Dados

Este projeto Java tem como objetivo ser uma prova de conceito e estudo de caso para automatizar a **leitura**, **criação** e **transferência** de tabelas e dados entre dois bancos de dados relacionais, incluindo a replicação de relacionamentos (chaves primárias e estrangeiras).

---

## Estrutura de Pacotes e Classes

### `com.vms.Main`
Classe principal que executa o processo completo de migração.

- **main**: Inicializa logs, conecta aos bancos de origem e destino e executa os métodos principais do `DatabaseMigrator`.

### `com.vms.db.migrator.DatabaseMigrator`
Coordena o processo de migração:

- **migrarTudo**: Executa em ordem: leitura de esquema, criação de tabelas, escrita de dados, e criação de relacionamentos.
- **criarTabelas**: Cria as tabelas no destino com base no esquema da origem.
- **escreverDados**: Copia os dados de cada tabela da origem para o destino.
- **criarRelacionamentos**: Replica constraints de chave estrangeira.

### `com.vms.db.reader.SchemaReader`
Lê os metadados do banco de dados:

- **listarTabelas**: Retorna todas as tabelas visíveis.
- **listarRelacionamentos**: Extrai as chaves estrangeiras.

### `com.vms.db.reader.DataReader`
Responsável por ler os dados das tabelas da origem:

- **lerDadosTabela**: Lê os registros de uma tabela.

### `com.vms.db.writer.DataWriter`
Responsável por inserir dados no banco de destino:

- **escreverDadosTabela**: Insere registros na tabela destino.

### `com.vms.db.model.SchemaAndTable`
Record que representa uma tabela com schema + nome.

### `com.vms.db.model.ForeignKeyInfo`
Record que representa uma chave estrangeira com:
- Tabela/coluna de origem
- Tabela/coluna referenciada

### `com.vms.db.connector.DatabaseConnector`
Interface para abstrair conexão a diferentes SGBDs.

### `com.vms.db.connector.ConnectionFactory`
Fábrica para criar conexões usando implementações específicas.

### `com.vms.db.connector.oracle.OracleConnectionFactory`
Conecta a bancos Oracle.

### `com.vms.db.connector.postgres.PostgresConnectionFactory`
Conecta a bancos PostgreSQL.

### `com.vms.util.LogConfig`
Configura o logging em arquivo.

---

## Vantagens da Arquitetura
- Estrutura modular e separada por responsabilidade.
- Fácil extensão para outros bancos.
- Uso de `DatabaseMetaData` para leitura precisa de esquema.
- Registros (records) deixam os modelos imutáveis e concisos.

---

## Exemplo de Uso

1. **Configurar os arquivos de conexão**

Edite os arquivos em  `src/main/resources/` com os dados de acesso aos bancos:

```properties
# origem.properties
db.url=myurl.com
db.port=6790
db.banco=postgres
db.user=my user
db.password=Senha
```

```properties
# destino.properties
db.url=localhost
db.port=5432
db.banco=postgres
db.user=postgres
db.password=Senha
```

## Vídeo de demonstração

[https://youtu.be/SaJIGLae4oo?si=tHcnLBXGgPpMXGYV](https://www.youtube.com/watch?v=IFLNeoCL8tw)
