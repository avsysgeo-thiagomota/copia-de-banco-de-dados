package com.vms.db.reader;

import java.sql.*;
import java.util.*;

public class DataReader {
    private final Connection connection;

    public DataReader(Connection connection) {
        this.connection = connection;
    }

    /**
     * Lê todos os dados de uma tabela e retorna como lista de mapas (coluna -> valor)
     *
     * @param tabela nome da tabela a ser lida
     * @return lista de linhas com seus respectivos dados
     */
    public List<Map<String, Object>> lerDados(String tabela) {
        try (
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM " + tabela)
        ) {
            List<Map<String, Object>> dados = new ArrayList<>();
            ResultSetMetaData meta = rs.getMetaData();
            int colunas = meta.getColumnCount();

            while (rs.next()) {
                Map<String, Object> linha = new HashMap<>();
                for (int i = 1; i <= colunas; i++) {
                    linha.put(meta.getColumnName(i), rs.getObject(i));
                }
                dados.add(linha);
            }

            return dados;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao ler dados da tabela " + tabela + "\n" + e.getMessage(), e);
        }
    }
}