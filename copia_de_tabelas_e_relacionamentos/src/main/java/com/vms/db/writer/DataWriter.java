package com.vms.db.writer;

import java.sql.*;
import java.util.*;

public class DataWriter {
    private final Connection connection;

    public DataWriter(Connection connection) {
        this.connection = connection;
    }

    public void inserirDados(String tabela, List<Map<String, Object>> dados) throws SQLException {
        if (dados.isEmpty()) return;

        String[] colunas = dados.get(0).keySet().toArray(new String[0]);
        StringBuilder sql = new StringBuilder("INSERT INTO " + tabela + " (");
        sql.append(String.join(", ", colunas));
        sql.append(") VALUES (");
        sql.append("?,".repeat(colunas.length));
        sql.setLength(sql.length() - 1);
        sql.append(")");

        try (PreparedStatement pstmt = connection.prepareStatement(sql.toString())) {
            for (Map<String, Object> linha : dados) {
                for (int i = 0; i < colunas.length; i++) {
                    pstmt.setObject(i + 1, linha.get(colunas[i]));
                }
                pstmt.executeUpdate();
            }
        }
    }
}
