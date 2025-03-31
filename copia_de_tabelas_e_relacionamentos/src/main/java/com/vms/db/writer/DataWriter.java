package com.vms.db.writer;

import com.vms.db.model.SchemaAndTable;
import com.vms.db.model.ForeignKeyInfo;
import com.vms.db.reader.SchemaReader;

import java.sql.*;
import java.util.*;
import java.util.logging.Logger;

public class DataWriter {
    private final Connection connection;
    private static final Logger logger = Logger.getLogger(DataWriter.class.getName());

    public DataWriter(Connection connection)
    {
        this.connection = connection;
    }

    /**
     * Insere dados em massa na tabela especificada.
     * Se a tabela ainda não existir, ela será criada dinamicamente.
     */
    public void inserirDados(SchemaAndTable tabela, List<Map<String, Object>> dados)
    {
        if (!TabelaCriada(tabela))
            criaTabela(tabela, dados);

        if (dados.isEmpty()) return;

        final String[] colunas = dados.get(0).keySet().toArray(new String[0]);

        // Monta a instrução SQL para inserção
        StringBuilder sql = new StringBuilder("INSERT INTO " + tabela + " (");
        sql.append(String.join(", ", colunas)).append(") VALUES (");
        sql.append("?, ".repeat(colunas.length));
        sql.setLength(sql.length() - 2);
        sql.append(")");

        // Executa a inserção usando PreparedStatement
        try (PreparedStatement pstmt = connection.prepareStatement(sql.toString()))
        {
            for (Map<String, Object> linha : dados)
            {
                for (int i = 0; i < colunas.length; i++)
                {
                    pstmt.setObject(i + 1, linha.get(colunas[i]));
                }
                pstmt.executeUpdate();
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Erro ao inserir dados na tabela\n" + e.getMessage(), e);
        }
    }

    /**
     * Verifica se a tabela já existe no banco.
     */
    private boolean TabelaCriada(SchemaAndTable tabela)
    {
        try (ResultSet rs = connection.getMetaData().getTables(null, tabela.schema(), tabela.tableName(), null))
        {
            return rs.next();
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Erro ao verificar existência da tabela\n" + e.getMessage(), e);
        }
    }

    /**
     * Verifica se a tabela já possui chave primária.
     */
//    private boolean temChavePrimaria(SchemaAndTable tabela) {
//        try (ResultSet rs = connection.getMetaData().getPrimaryKeys(null, tabela.schema(), tabela.tableName())) {
//            return rs.next();
//        } catch (SQLException e) {
//            throw new RuntimeException("Erro ao verificar chave primária\n" + e.getMessage(), e);
//        }
//    }

    /**
     * Cria a tabela com base na estrutura do primeiro registro dos dados fornecidos.
     * Detecta tipos SQL dinamicamente e adiciona chave primária se possível.
     */
    private void criaTabela(SchemaAndTable tabela, List<Map<String, Object>> dados) {

        // Verifica se o schema existe
        // Se não existir, cria o schema
        try (ResultSet rs = connection.getMetaData().getSchemas()) {
            boolean schemaExiste = false;

            while (rs.next()) {
                if (rs.getString("TABLE_SCHEM").equalsIgnoreCase(tabela.schema())) {
                    schemaExiste = true;
                    break;
                }
            }

            if (!schemaExiste) {
                String query = String.format("CREATE SCHEMA %s", tabela.schema());
                try (Statement stmt = connection.createStatement()) {
                    stmt.executeUpdate(query);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar ou criar schema " + tabela.schema() + "\n" + e.getMessage(), e);
        }

        if (dados.isEmpty()) {
            throw new IllegalArgumentException("Lista de dados vazia: não é possível criar tabela sem base.");
        }

        Map<String, Object> exemplo = dados.get(0);
        StringBuilder sql = new StringBuilder("CREATE TABLE " + tabela + " (");

        String chavePrimaria = null;
        for (Map.Entry<String, Object> coluna : exemplo.entrySet()) {
            String nomeColuna = coluna.getKey();
            if (chavePrimaria == null && nomeColuna.equalsIgnoreCase("id")) {
                chavePrimaria = nomeColuna;
            }

            sql.append(nomeColuna)
                    .append(" ")
                    .append(detectaTipoSQL(coluna.getValue()))
                    .append(", ");
        }

        sql.setLength(sql.length() - 2); // Remove a última vírgula
        sql.append(")");

        try (Statement stmt = connection.createStatement())
        {
            stmt.execute(sql.toString());
            logger.info("Tabela " + tabela + " criada com sucesso.");
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Erro ao criar a tabela\n" + e.getMessage(), e);
        }
    }

    /**
     * Deduz o tipo SQL apropriado com base no tipo de objeto Java.
     */
    private String detectaTipoSQL(Object valor)
    {
        if (valor == null) return "TEXT";
        if (valor instanceof Integer) return "INTEGER";
        if (valor instanceof Long) return "BIGINT";
        if (valor instanceof Double || valor instanceof Float) return "DOUBLE PRECISION";
        if (valor instanceof Boolean) return "BOOLEAN";
        if (valor instanceof java.sql.Date) return "DATE";
        if (valor instanceof java.sql.Timestamp) return "TIMESTAMP";
        return "TEXT";
    }
}