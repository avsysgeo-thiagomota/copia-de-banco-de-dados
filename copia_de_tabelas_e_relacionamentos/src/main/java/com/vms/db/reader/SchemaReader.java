package com.vms.db.reader;

import com.vms.db.model.ForeignKeyInfo;
import com.vms.db.model.SchemaAndTable;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SchemaReader {
    private final Connection connection;
    private final DatabaseMetaData meta;

    public SchemaReader(Connection connection) {
        this.connection = connection;
        try
        {
            this.meta = connection.getMetaData();
            System.out.println("this.meta: " + this.meta.getSchemas().toString());
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Erro ao obter metadados do banco de dados\n" + e.getMessage(), e);
        }
    }

    public List<SchemaAndTable> listarTabelas()
    {
        try
        {
            List<SchemaAndTable> tabelas = new ArrayList<>();
            ResultSet rs = meta.getTables(null, null, "%", new String[]{"TABLE"});

            while (rs.next())
            {
                tabelas.add(new SchemaAndTable(rs.getString("TABLE_SCHEM"), rs.getString("TABLE_NAME")));
            }

            rs.close();
            return tabelas;
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Erro ao listar tabelas do banco de dados\n" + e.getMessage(), e);
        }
    }

    public List<ForeignKeyInfo> listarRelacionamentos()
    {
        try {
            List<ForeignKeyInfo> fks = new ArrayList<>();
            for (SchemaAndTable tabela : listarTabelas())
            {
                ResultSet rs = meta.getImportedKeys(null, tabela.schema(), tabela.tableName());

                while (rs.next())
                {
                    fks.add(
                        new ForeignKeyInfo(
                            new SchemaAndTable(rs.getString("FKTABLE_SCHEM"), rs.getString("FKTABLE_NAME")),
                            rs.getString("FKCOLUMN_NAME"),
                            new SchemaAndTable(rs.getString("PKTABLE_SCHEM"), rs.getString("PKTABLE_NAME")),
                            rs.getString("PKCOLUMN_NAME")
                        )
                    );
                }
                rs.close();
            }
            return fks;
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Erro ao listar relacionamentos do banco de dados\n" + e.getMessage(), e);
        }
    }
}