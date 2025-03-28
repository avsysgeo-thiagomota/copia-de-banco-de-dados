package com.vms.db;

import com.vms.db.migrator.DatabaseMigrator;
import com.vms.db.reader.SchemaReader;
import com.vms.db.reader.DataReader;
import com.vms.db.writer.DataWriter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.*;

import static org.mockito.Mockito.*;

class DatabaseMigratorTest {

    private Connection origemConn;
    private Connection destinoConn;
    private SchemaReader schemaReader;
    private DataReader dataReader;
    private DataWriter dataWriter;

    @BeforeEach
    void setup() {
        origemConn = mock(Connection.class);
        destinoConn = mock(Connection.class);

        schemaReader = mock(SchemaReader.class);
        dataReader = mock(DataReader.class);
        dataWriter = mock(DataWriter.class);
    }

    @Test
    void deveMigrarTodasAsTabelas() throws Exception {
        // Simular tabela e dados
        List<String> tabelas = List.of("clientes");
        List<Map<String, Object>> dadosClientes = List.of(
                Map.of("id", 1, "nome", "Ana"),
                Map.of("id", 2, "nome", "Bruno")
        );

        when(schemaReader.listarTabelas()).thenReturn(tabelas);
        when(dataReader.lerDados("clientes")).thenReturn(dadosClientes);

        // Criar migrador com injeção de mocks via construtor "manual"
        DatabaseMigrator migrator = new DatabaseMigrator(schemaReader, dataReader, dataWriter);

        // Executar
        migrator.migrarTudo();

        // Verificações
        verify(schemaReader, times(1)).listarTabelas();
        verify(dataReader, times(1)).lerDados("clientes");
        verify(dataWriter, times(1)).inserirDados("clientes", dadosClientes);
    }
}
