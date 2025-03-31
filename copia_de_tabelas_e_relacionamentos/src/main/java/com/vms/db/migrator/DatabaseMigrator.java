package com.vms.db.migrator;

import com.vms.db.model.SchemaTables;
import com.vms.db.reader.DataReader;
import com.vms.db.model.ForeignKeyInfo;
import com.vms.db.reader.SchemaReader;
import com.vms.db.writer.DataWriter;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
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

    public void migrarTudo() {
        try {
            SchemaReader schemaReader = new SchemaReader(origemConn);
            DataReader dataReader = new DataReader(origemConn);
            DataWriter dataWriter = new DataWriter(destinoConn);

            List<SchemaTables> tabelas = schemaReader.listarTabelas();
            List<ForeignKeyInfo> fks = schemaReader.listarRelacionamentos();


            for (SchemaTables tabela : tabelas) {
                try {

                    

                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Erro ao migrar tabela '" + tabela + "': " + e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao executar a migração de banco de dados\n" + e.getMessage(), e);
        }
    }
}