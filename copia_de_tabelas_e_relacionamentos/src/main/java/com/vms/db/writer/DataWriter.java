package com.vms.db.writer;

import java.sql.*;
import java.util.*;

public class DataWriter {
    private final Connection connection;

    public DataWriter(Connection connection) {
        this.connection = connection;
    }

    /**
     * Insere os dados em massa na tabela destino.
     *
     * @param tabela Nome da tabela de destino
     * @param dados  Lista de linhas (coluna → valor) a serem inseridas
     */
    public void inserirDados(String tabela, List<Map<String, Object>> dados) {
        if (dados.isEmpty()) return;

        // Extrai os nomes das colunas a partir da primeira linha da lista
        final String[] colunas = dados.get(0).keySet().toArray(new String[0]);

        // Começa a montar a SQL base
        StringBuilder sql = new StringBuilder("INSERT INTO " + tabela + " (");
        sql.append(String.join(", ", colunas));
        sql.append(") VALUES (");

        //Corrigido: adiciona "?, " (com espaço após a vírgula)
        sql.append("?, ".repeat(colunas.length));

        //Remove a última vírgula + espaço (2 caracteres)
        sql.setLength(sql.length() - 2);

        sql.append(")");

        // Prepara e executa a SQL com PreparedStatement
        try (PreparedStatement pstmt = connection.prepareStatement(sql.toString())) {
            for (Map<String, Object> linha : dados) {
                for (int i = 0; i < colunas.length; i++) {
                    pstmt.setObject(i + 1, linha.get(colunas[i]));
                }
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir dados na tabela " + tabela + "\n" + e.getMessage(), e);
        }
    }

}