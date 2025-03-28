package com.vms.db.reader;

import com.vms.db.model.ForeignKeyInfo;

import java.sql.*;
import java.util.*;

public class SchemaReader {
    private final Connection connection;

    public SchemaReader(Connection connection) {
        this.connection = connection;
    }

    public List<String> listarTabelas() throws SQLException {
        List<String> tabelas = new ArrayList<>();
        DatabaseMetaData meta = connection.getMetaData();
        ResultSet rs = meta.getTables(null, null, "%", new String[]{"TABLE"});

        while (rs.next()) {
            tabelas.add(rs.getString("TABLE_NAME"));
        }

        rs.close();
        return tabelas;
    }

    public List<ForeignKeyInfo> listarRelacionamentos() throws SQLException {
        List<ForeignKeyInfo> fks = new ArrayList<>();
        for (String tabela : listarTabelas()) {
            DatabaseMetaData meta = connection.getMetaData();
            ResultSet rs = meta.getImportedKeys(null, null, tabela);

            while (rs.next()) {
                fks.add(new ForeignKeyInfo(
                        rs.getString("FKTABLE_NAME"),
                        rs.getString("FKCOLUMN_NAME"),
                        rs.getString("PKTABLE_NAME"),
                        rs.getString("PKCOLUMN_NAME")
                ));
            }

            rs.close();
        }
        return fks;
    }
}
