package com.vms.db;

import com.vms.db.reader.DataReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DataReaderTest {

    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;
    private ResultSetMetaData metaData;

    @BeforeEach
    void setup() throws Exception {
        connection = mock(Connection.class);
        statement = mock(Statement.class);
        resultSet = mock(ResultSet.class);
        metaData = mock(ResultSetMetaData.class);

        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeQuery("SELECT * FROM clientes")).thenReturn(resultSet);
        when(resultSet.getMetaData()).thenReturn(metaData);
    }

    @Test
    void deveLerDadosDaTabela() throws Exception {
        // Simular metadados da tabela
        when(metaData.getColumnCount()).thenReturn(2);
        when(metaData.getColumnName(1)).thenReturn("id");
        when(metaData.getColumnName(2)).thenReturn("nome");

        // Simular duas linhas
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getObject(1)).thenReturn(1, 2);
        when(resultSet.getObject(2)).thenReturn("Ana", "Bruno");

        DataReader reader = new DataReader(connection);
        List<Map<String, Object>> dados = reader.lerDados("clientes");

        assertEquals(2, dados.size());

        Map<String, Object> linha1 = dados.get(0);
        assertEquals(1, linha1.get("id"));
        assertEquals("Ana", linha1.get("nome"));

        Map<String, Object> linha2 = dados.get(1);
        assertEquals(2, linha2.get("id"));
        assertEquals("Bruno", linha2.get("nome"));

        verify(resultSet).close();
        verify(statement).close();
    }
}
