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

    // Mocks para simular os objetos de JDBC
    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;
    private ResultSetMetaData metaData;

    // Inicializa os mocks antes de cada teste
    @BeforeEach
    void setup() throws Exception {
        connection = mock(Connection.class);
        statement = mock(Statement.class);
        resultSet = mock(ResultSet.class);
        metaData = mock(ResultSetMetaData.class);

        // Configura os comportamentos esperados
        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeQuery("SELECT * FROM clientes")).thenReturn(resultSet);
        when(resultSet.getMetaData()).thenReturn(metaData);
    }

    // Testa se os dados são lidos corretamente da tabela simulada
    @Test
    void deveLerDadosDaTabela() throws Exception {
        // Simula uma tabela com duas colunas: id e nome
        when(metaData.getColumnCount()).thenReturn(2);
        when(metaData.getColumnName(1)).thenReturn("id");
        when(metaData.getColumnName(2)).thenReturn("nome");

        // Simula duas linhas retornadas pela consulta
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getObject(1)).thenReturn(1, 2);
        when(resultSet.getObject(2)).thenReturn("Ana", "Bruno");

        DataReader reader = new DataReader(connection);
        List<Map<String, Object>> dados = reader.lerDados("clientes");

        // Valida que duas linhas foram retornadas
        assertEquals(2, dados.size());

        // Valida o conteúdo da primeira linha
        Map<String, Object> linha1 = dados.get(0);
        assertEquals(1, linha1.get("id"));
        assertEquals("Ana", linha1.get("nome"));

        // Valida o conteúdo da segunda linha
        Map<String, Object> linha2 = dados.get(1);
        assertEquals(2, linha2.get("id"));
        assertEquals("Bruno", linha2.get("nome"));

        // Verifica se os recursos foram fechados corretamente
        verify(resultSet).close();
        verify(statement).close();
    }

    // Testa se uma exceção SQL é tratada corretamente no padrão do projeto
    @Test
    void deveLancarExcecaoAoFalharNaLeituraDaTabela() throws Exception {
        when(connection.createStatement()).thenThrow(new SQLException("Falha simulada"));

        DataReader reader = new DataReader(connection);

        RuntimeException excecao = assertThrows(RuntimeException.class, () -> {
            reader.lerDados("clientes");
        });

        assertTrue(excecao.getMessage().contains("Erro ao ler dados da tabela clientes"));
    }
}