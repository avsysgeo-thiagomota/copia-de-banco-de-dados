package com.vms.db;

import com.vms.db.connector.DatabaseConnector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseConnectorTest {

    private final String testConfigPath = "src/test/resources/testdb.properties";

    @BeforeEach
    void setup() throws IOException {
        // Criar arquivo de teste temporário
        try (FileWriter writer = new FileWriter(testConfigPath)) {
            writer.write("db.url=localhost\n");
            writer.write("db.port=5432\n");
            writer.write("db.banco=postgres\n");
            writer.write("db.user=postgres\n");
            writer.write("db.password=123456\n");
        }
    }

    @Test
    void deveConectarComSucesso() {
        DatabaseConnector connector = new DatabaseConnector(testConfigPath);
        Connection connection = connector.getConnection();

        assertNotNull(connection, "A conexão não deveria ser nula");

        try {
            assertFalse(connection.isClosed(), "A conexão não deveria estar fechada");
            connection.close();
        } catch (Exception e) {
            fail("Erro ao verificar ou fechar a conexão: " + e.getMessage());
        }
    }
}
