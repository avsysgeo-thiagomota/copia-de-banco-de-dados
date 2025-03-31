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

    // Prepara os mocks antes de cada teste
    @BeforeEach
    void setup() {
        origemConn = mock(Connection.class);
        destinoConn = mock(Connection.class);

        schemaReader = mock(SchemaReader.class);
        dataReader = mock(DataReader.class);
        dataWriter = mock(DataWriter.class);
    }

    // Testa o fluxo completo de migração com sucesso
    @Test
    void deveMigrarTodasAsTabelas() throws Exception {
        List<String> tabelas = List.of("clientes");
        List<Map<String, Object>> dadosClientes = List.of(
                Map.of("id", 1, "nome", "Ana"),
                Map.of("id", 2, "nome", "Bruno")
        );

        when(schemaReader.listarTabelas()).thenReturn(tabelas);
        when(dataReader.lerDados("clientes")).thenReturn(dadosClientes);

        DatabaseMigrator migrator = new DatabaseMigrator(schemaReader, dataReader, dataWriter);
        migrator.migrarTudo();

        verify(schemaReader).listarTabelas();
        verify(dataReader).lerDados("clientes");
        verify(dataWriter).inserirDados("clientes", dadosClientes);
    }

    // Testa se a migração continua mesmo quando uma tabela falha
    @Test
    void deveContinuarMigracaoMesmoComErroEmUmaTabela() throws Exception {
        List<String> tabelas = List.of("clientes", "pedidos");

        when(schemaReader.listarTabelas()).thenReturn(tabelas);
        when(dataReader.lerDados("clientes")).thenThrow(new RuntimeException("Erro simulado"));

        List<Map<String, Object>> dadosPedidos = List.of(
                Map.of("id", 100, "valor", 200.0)
        );
        when(dataReader.lerDados("pedidos")).thenReturn(dadosPedidos);

        DatabaseMigrator migrator = new DatabaseMigrator(schemaReader, dataReader, dataWriter);
        migrator.migrarTudo();

        verify(schemaReader).listarTabelas();
        verify(dataReader).lerDados("clientes");
        verify(dataReader).lerDados("pedidos");
        verify(dataWriter).inserirDados("pedidos", dadosPedidos);
        verify(dataWriter, never()).inserirDados(eq("clientes"), any());
    }
}