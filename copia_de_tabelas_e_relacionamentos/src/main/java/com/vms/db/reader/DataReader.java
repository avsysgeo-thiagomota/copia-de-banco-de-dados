package com.vms.db.reader;

import java.sql.*;
import java.util.*;

public class DataReader {
    private final Connection connection;

    public DataReader(Connection connection) {
        this.connection = connection;
    }

    public List<Map<String, Object>> lerDados(String tabela) throws SQLException {
        List<Map<String, Object>> dados = new ArrayList<>();
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM " + tabela);
        ResultSetMetaData meta = rs.getMetaData();
        int colunas = meta.getColumnCount();

        while (rs.next()) {
            Map<String, Object> linha = new HashMap<>();
            for (int i = 1; i <= colunas; i++) {
                linha.put(meta.getColumnName(i), rs.getObject(i));
            }
            dados.add(linha);
        }

        rs.close();
        stmt.close();
        return dados;
    }
}
