package com.vms.db.migrator;

import com.vms.db.model.SchemaAndTable;
import com.vms.db.reader.DataReader;
import com.vms.db.model.ForeignKeyInfo;
import com.vms.db.reader.SchemaReader;
import com.vms.db.writer.DataWriter;

import java.sql.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseMigrator {
    private static final Logger logger = Logger.getLogger(DatabaseMigrator.class.getName());
    private final Connection origemConn;
    private final Connection destinoConn;

    public DatabaseMigrator(Connection origemConn, Connection destinoConn) {
        this.origemConn = origemConn;
        this.destinoConn = destinoConn;
    }

    public void migrarTudo()
    {
        SchemaReader schemaReader = new SchemaReader(origemConn);
        DataReader dataReader = new DataReader(origemConn);
        DataWriter dataWriter = new DataWriter(destinoConn);

        try
        {
            List<SchemaAndTable> tabelas = schemaReader.listarTabelas();

            for (SchemaAndTable tabela : tabelas)
            {
                try
                {
                    var dados = dataReader.lerDados(tabela);
                    dataWriter.inserirDados(tabela, dados);
                }
                catch (Exception e)
                {
                    logger.log(Level.SEVERE, "Erro ao migrar tabela '" + tabela + "': " + e.getMessage(), e);
                }
            }

            new RelationshipBuilder(origemConn, destinoConn).criarRelacionamentos(schemaReader);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Erro ao executar ao criar relacionamentos\n" + e.getMessage(), e);
        }
    }
}