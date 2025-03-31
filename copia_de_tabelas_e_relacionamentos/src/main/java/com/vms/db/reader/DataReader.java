package com.vms.db.reader;

import com.vms.db.migrator.DatabaseMigrator;

import java.sql.*;
import java.util.*;
import java.util.logging.Logger;

public class DataReader {
    private static final Logger logger = Logger.getLogger(DataReader.class.getName());
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
        try (Statement stmt = connection.createStatement()) {
            String quote = connection.getMetaData().getIdentifierQuoteString().trim();
            String query = "SELECT * FROM " + tabela;

            try {
                logger.info("Tentando SELECT sem aspas: " + query);
                return executarQuery(stmt, query);
            } catch (SQLException e1) {
                // tenta com aspas
                String tabelaComAspas = quote + tabela.replace(quote, quote + quote) + quote;
                String queryComAspas = "SELECT * FROM " + tabelaComAspas;
                logger.warning("Tentando SELECT com aspas: " + queryComAspas);
                try {
                    return executarQuery(stmt, queryComAspas);
                } catch (SQLException e2) {
                    throw new RuntimeException("Erro ao ler dados da tabela " + tabela + "\n" + e2.getMessage(), e2);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro de conexão ao ler dados da tabela " + tabela + "\n" + e.getMessage(), e);
        }
    }

    private List<Map<String, Object>> executarQuery(Statement stmt, String query) throws SQLException {
        ResultSet rs = stmt.executeQuery(query);
        ResultSetMetaData meta = rs.getMetaData();
        int colunas = meta.getColumnCount();
        List<Map<String, Object>> resultados = new ArrayList<>();

        while (rs.next()) {
            Map<String, Object> linha = new LinkedHashMap<>();
            for (int i = 1; i <= colunas; i++) {
                linha.put(meta.getColumnName(i), rs.getObject(i));
            }
            resultados.add(linha);
        }

        rs.close();
        return resultados;
    }


}