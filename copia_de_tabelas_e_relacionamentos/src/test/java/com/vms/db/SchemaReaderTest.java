package com.vms.db;

import com.vms.db.model.ForeignKeyInfo;
import com.vms.db.reader.SchemaReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SchemaReaderTest {

    private Connection connection;
    private DatabaseMetaData metaData;

    @BeforeEach
    void setup() throws Exception {
        connection = mock(Connection.class);
        metaData = mock(DatabaseMetaData.class);
        when(connection.getMetaData()).thenReturn(metaData);
    }

    @Test
    void deveListarTabelasCorretamente() throws Exception {
        ResultSet rs = mock(ResultSet.class);

        when(metaData.getTables(null, null, "%", new String[]{"TABLE"})).thenReturn(rs);
        when(rs.next()).thenReturn(true, true, false);
        when(rs.getString("TABLE_NAME")).thenReturn("clientes", "pedidos");

        SchemaReader reader = new SchemaReader(connection);
        List<String> tabelas = reader.listarTabelas();

        assertEquals(2, tabelas.size());
        assertTrue(tabelas.contains("clientes"));
        assertTrue(tabelas.contains("pedidos"));

        verify(rs, times(3)).next();
        rs.close();
    }

    @Test
    void deveListarRelacionamentosCorretamente() throws Exception {
        ResultSet tabelasRS = mock(ResultSet.class);
        ResultSet fksRS = mock(ResultSet.class);

        // Simulando tabela "pedidos"
        when(metaData.getTables(null, null, "%", new String[]{"TABLE"})).thenReturn(tabelasRS);
        when(tabelasRS.next()).thenReturn(true, false);
        when(tabelasRS.getString("TABLE_NAME")).thenReturn("pedidos");

        // Simulando FK em "pedidos"
        when(metaData.getImportedKeys(null, null, "pedidos")).thenReturn(fksRS);
        when(fksRS.next()).thenReturn(true, false);
        when(fksRS.getString("FKTABLE_NAME")).thenReturn("pedidos");
        when(fksRS.getString("FKCOLUMN_NAME")).thenReturn("cliente_id");
        when(fksRS.getString("PKTABLE_NAME")).thenReturn("clientes");
        when(fksRS.getString("PKCOLUMN_NAME")).thenReturn("id");

        SchemaReader reader = new SchemaReader(connection);
        List<ForeignKeyInfo> fks = reader.listarRelacionamentos();

        assertEquals(1, fks.size());
        ForeignKeyInfo fk = fks.get(0);
        assertEquals("pedidos", fk.tabelaOrigem);
        assertEquals("cliente_id", fk.colunaOrigem);
        assertEquals("clientes", fk.tabelaReferencia);
        assertEquals("id", fk.colunaReferencia);
    }
}
