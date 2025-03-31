package com.vms.db.writer;

import java.sql.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class DataWriter {
    private final Connection connection;
    private static final Logger logger = Logger.getLogger(DataWriter.class.getName());

    public DataWriter(Connection connection) {
        this.connection = connection;
    }

    /**
     * Insere os dados em massa na tabela destino.
     *
     * @param tabela Nome da tabela de destino
     * @param dados  Lista de linhas (coluna → valor) a serem inseridas
     */
    public void inserirDados(String tabela, List<Map<String, Object>> dados)
    {
        if (dados.isEmpty()) return;

        try
        {
            String quote = connection.getMetaData().getIdentifierQuoteString().trim();

            final String[] colunas = dados.get(0).keySet().toArray(new String[0]);

            // Escapa os nomes das colunas
            String colunasFormatadas = Arrays.stream(colunas)
                    .map(col -> quote + col.replace(quote, quote + quote) + quote)
                    .collect(Collectors.joining(", "));

            // Cria a parte de valores (?, ?, ?...)
            String placeholders = String.join(", ", Collections.nCopies(colunas.length, "?"));

            // Escapa nome da tabela
            String tabelaSegura = quote + tabela.replace(quote, quote + quote) + quote;

            String sql = "INSERT INTO " + tabelaSegura + " (" + colunasFormatadas + ") VALUES (" + placeholders + ")";

            try (PreparedStatement pstmt = connection.prepareStatement(sql))
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

        }
        catch (SQLException e)
        {
            throw new RuntimeException("Erro ao inserir dados na tabela " + tabela + "\n" + e.getMessage(), e);
        }
    }
}