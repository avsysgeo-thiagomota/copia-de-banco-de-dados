package com.vms.db;

import com.vms.db.model.ForeignKeyInfo;
import com.vms.db.reader.SchemaReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SchemaReaderTest {

    // Mocks para simular a conexão com o banco e os resultados de metadata
    private Connection connection;
    private DatabaseMetaData metaData;
    private ResultSet rs;

    // Executa antes de cada teste, preparando os mocks básicos
    @BeforeEach
    void setup() throws SQLException {
        connection = mock(Connection.class);                    // Simula uma conexão de banco
        metaData = mock(DatabaseMetaData.class);                // Simula os metadados da conexão
        rs = mock(ResultSet.class);                             // Simula o resultado de uma query
        when(connection.getMetaData()).thenReturn(metaData);    // Diz que a conexão retorna o metadata simulado
    }

    // Testa se listarTabelas() retorna corretamente os nomes das tabelas
    @Test
    void deveListarTabelasCorretamente() throws SQLException {
        // Simula que há duas tabelas: "clientes" e "pedidos"
        when(metaData.getTables(null, null, "%", new String[]{"TABLE"})).thenReturn(rs);
        when(rs.next()).thenReturn(true, true, false); // três chamadas: duas verdadeiras, depois false (fim)
        when(rs.getString("TABLE_NAME")).thenReturn("clientes", "pedidos");

        SchemaReader reader = new SchemaReader(connection);
        List<String> tabelas = reader.listarTabelas();

        // Verifica se as tabelas retornadas são exatamente essas
        assertEquals(Arrays.asList("clientes", "pedidos"), tabelas);

        // Verifica se o ResultSet foi fechado
        verify(rs).close();
    }

    // Testa se listarRelacionamentos() retorna corretamente os FKs encontrados
    @Test
    void deveListarRelacionamentosCorretamente() throws SQLException {
        // Simula que a única tabela no banco é "itens_pedido"
        when(metaData.getTables(null, null, "%", new String[]{"TABLE"})).thenReturn(rs);
        when(rs.next()).thenReturn(true, false); // apenas uma tabela
        when(rs.getString("TABLE_NAME")).thenReturn("itens_pedido");

        // Simula a existência de uma FK de pedido_id ? pedidos.id
        ResultSet fkRs = mock(ResultSet.class);
        when(metaData.getImportedKeys(null, null, "itens_pedido")).thenReturn(fkRs);
        when(fkRs.next()).thenReturn(true, false);
        when(fkRs.getString("FKTABLE_NAME")).thenReturn("itens_pedido");
        when(fkRs.getString("FKCOLUMN_NAME")).thenReturn("pedido_id");
        when(fkRs.getString("PKTABLE_NAME")).thenReturn("pedidos");
        when(fkRs.getString("PKCOLUMN_NAME")).thenReturn("id");

        SchemaReader reader = new SchemaReader(connection);
        List<ForeignKeyInfo> fks = reader.listarRelacionamentos();

        // Verifica se uma FK foi encontrada e os dados estão corretos
        assertEquals(1, fks.size());
        ForeignKeyInfo fk = fks.get(0);
        assertEquals("itens_pedido", fk.tabelaOrigem());
        assertEquals("pedido_id", fk.colunaOrigem());
        assertEquals("pedidos", fk.tabelaReferencia());
        assertEquals("id", fk.colunaReferencia());

        // Verifica se os ResultSets foram fechados
        verify(rs).close();
        verify(fkRs).close();
    }

    // Testa se o método listarTabelas() trata erros corretamente e lança RuntimeException
    @Test
    void deveLancarExcecaoAoFalharNaLeituraDeTabelas() throws SQLException {
        // Simula que uma exceção SQL é lançada ao tentar ler as tabelas
        when(metaData.getTables(null, null, "%", new String[]{"TABLE"}))
                .thenThrow(new SQLException("Erro simulado"));

        SchemaReader reader = new SchemaReader(connection);

        // Verifica se uma RuntimeException é lançada com a mensagem esperada
        RuntimeException thrown = assertThrows(RuntimeException.class, reader::listarTabelas);
        assertTrue(thrown.getMessage().contains("Erro ao listar tabelas do banco de dados"));
    }
}