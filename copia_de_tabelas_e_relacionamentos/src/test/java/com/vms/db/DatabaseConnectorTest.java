package com.vms.db;

import com.vms.db.connector.ConnectionFactory;
import com.vms.db.connector.DatabaseConnector;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DatabaseConnectorTest {

    @Test
    void deveRetornarConexaoDaFactory() {
        // Arrange
        Connection fakeConnection = mock(Connection.class);
        ConnectionFactory factoryMock = mock(ConnectionFactory.class);
        when(factoryMock.createConnection()).thenReturn(fakeConnection);

        DatabaseConnector connector = new DatabaseConnector(factoryMock::createConnection);

        // Act
        Connection conn = connector.getConnection();

        // Assert
        assertNotNull(conn, "A conexão não deveria ser nula");
        assertEquals(fakeConnection, conn, "A conexão retornada deve ser a da factory");
        verify(factoryMock, times(1)).createConnection();
    }
}