package com.vms.db.migrator;

import com.vms.db.reader.DataReader;
import com.vms.db.reader.SchemaReader;
import com.vms.db.writer.DataWriter;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class DatabaseMigrator {
    private final SchemaReader schemaReader;
    private final DataReader dataReader;
    private final DataWriter dataWriter;

    public DatabaseMigrator(Connection origem, Connection destino) {
        this.schemaReader = new SchemaReader(origem);
        this.dataReader = new DataReader(origem);
        this.dataWriter = new DataWriter(destino);
    }

    public DatabaseMigrator(SchemaReader schemaReader, DataReader dataReader, DataWriter dataWriter) {
        this.schemaReader = schemaReader;
        this.dataReader = dataReader;
        this.dataWriter = dataWriter;
    }

    public void migrarTudo() throws SQLException {
        List<String> tabelas = schemaReader.listarTabelas();
        for (String tabela : tabelas) {
            List<Map<String, Object>> dados = dataReader.lerDados(tabela);
            dataWriter.inserirDados(tabela, dados);
            System.out.println("Migrada: " + tabela + " (" + dados.size() + " linhas)");
        }
    }
}
