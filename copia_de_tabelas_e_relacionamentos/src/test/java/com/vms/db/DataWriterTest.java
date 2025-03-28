package com.vms.db;

import com.vms.db.writer.DataWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

class DataWriterTest {

    private Connection connection;
    private PreparedStatement pstmt;

    @BeforeEach
    void setup() throws Exception {
        connection = mock(Connection.class);
        pstmt = mock(PreparedStatement.class);

        when(connection.prepareStatement(anyString())).thenReturn(pstmt);
        when(pstmt.executeUpdate()).thenReturn(1); // simula execução bem-sucedida
    }

    @Test
    void deveInserirDadosComSucesso() throws Exception {
        // Simular dados
        Map<String, Object> row1 = new LinkedHashMap<>();
        row1.put("id", 1);
        row1.put("nome", "Ana");

        Map<String, Object> row2 = new LinkedHashMap<>();
        row2.put("id", 2);
        row2.put("nome", "Bruno");

        List<Map<String, Object>> dados = Arrays.asList(row1, row2);

        DataWriter writer = new DataWriter(connection);

        assertDoesNotThrow(() -> writer.inserirDados("clientes", dados));

        // Verifica que a query foi montada corretamente
        verify(connection).prepareStatement("INSERT INTO clientes (id, nome) VALUES (?, ?)");

        // Verifica que os valores foram passados corretamente
        verify(pstmt, times(1)).setObject(1, 1);
        verify(pstmt, times(1)).setObject(2, "Ana");
        verify(pstmt, times(1)).setObject(1, 2);
        verify(pstmt, times(1)).setObject(2, "Bruno");

        // Verifica que executeUpdate foi chamado para cada linha
        verify(pstmt, times(2)).executeUpdate();
    }

    @Test
    void deveIgnorarInsercaoSeListaVazia() throws Exception {
        DataWriter writer = new DataWriter(connection);
        writer.inserirDados("clientes", Collections.emptyList());

        verify(connection, never()).prepareStatement(anyString());
        verify(pstmt, never()).executeUpdate();
    }
}
