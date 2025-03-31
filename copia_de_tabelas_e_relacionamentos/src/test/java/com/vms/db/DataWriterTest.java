package com.vms.db;

import com.vms.db.writer.DataWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.DatabaseMetaData;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DataWriterTest {

    private Connection connection;
    private PreparedStatement pstmt;
    private DatabaseMetaData metaData;

    @BeforeEach
    void setup() throws Exception {
        connection = mock(Connection.class);
        pstmt = mock(PreparedStatement.class);
        metaData = mock(DatabaseMetaData.class);

        when(connection.prepareStatement(anyString())).thenReturn(pstmt);
        when(pstmt.executeUpdate()).thenReturn(1);
        when(connection.getMetaData()).thenReturn(metaData);
        when(metaData.getIdentifierQuoteString()).thenReturn("\"");
    }

    @Test
    void deveInserirDadosComSucesso() throws Exception {
        Map<String, Object> row1 = new LinkedHashMap<>();
        row1.put("id", 1);
        row1.put("nome", "Ana");

        Map<String, Object> row2 = new LinkedHashMap<>();
        row2.put("id", 2);
        row2.put("nome", "Bruno");

        List<Map<String, Object>> dados = Arrays.asList(row1, row2);

        DataWriter writer = new DataWriter(connection);

        assertDoesNotThrow(() -> writer.inserirDados("clientes", dados));

        verify(connection).prepareStatement("INSERT INTO \"clientes\" (\"id\", \"nome\") VALUES (?, ?)");

        verify(pstmt, times(1)).setObject(1, 1);
        verify(pstmt, times(1)).setObject(2, "Ana");
        verify(pstmt, times(1)).setObject(1, 2);
        verify(pstmt, times(1)).setObject(2, "Bruno");

        verify(pstmt, times(2)).executeUpdate();
    }

    @Test
    void deveIgnorarInsercaoSeListaVazia() throws Exception {
        DataWriter writer = new DataWriter(connection);

        writer.inserirDados("clientes", Collections.emptyList());

        verify(connection, never()).prepareStatement(anyString());
        verify(pstmt, never()).executeUpdate();
    }

    @Test
    void deveLancarExcecaoAoFalharNaInsercao() throws Exception {
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException("Erro simulado"));

        Map<String, Object> linha = new LinkedHashMap<>();
        linha.put("id", 1);
        linha.put("nome", "Ana");

        List<Map<String, Object>> dados = Collections.singletonList(linha);

        when(connection.getMetaData()).thenReturn(metaData);
        when(metaData.getIdentifierQuoteString()).thenReturn("\"");

        DataWriter writer = new DataWriter(connection);

        RuntimeException excecao = assertThrows(RuntimeException.class, () -> {
            writer.inserirDados("clientes", dados);
        });

        assertTrue(excecao.getMessage().contains("Erro ao inserir dados na tabela clientes"));
    }
}