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

            criarRelacionamentos(schemaReader);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Erro ao executar ao criar relacionamentos\n" + e.getMessage(), e);
        }
    }

    private boolean colunaEhUnicaOuPK(Connection conn, SchemaAndTable tabela, String coluna) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();

        // Verifica se a coluna é parte de uma PRIMARY KEY
        try (ResultSet rsPk = meta.getPrimaryKeys(null, tabela.schema(), tabela.tableName())) {
            while (rsPk.next()) {
                if (coluna.equals(rsPk.getString("COLUMN_NAME"))) {
                    return true;
                }
            }
        }

        // Verifica se a coluna tem UNIQUE
        try (ResultSet rsUnique = meta.getIndexInfo(null, tabela.schema(), tabela.tableName(), true, false)) {
            while (rsUnique.next()) {
                if (coluna.equals(rsUnique.getString("COLUMN_NAME"))) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Cria as constraints de chave estrangeira com base nos relacionamentos detectados pelo SchemaReader.
     */
    public void criarRelacionamentos(SchemaReader schemaReaderOrigin)
    {
        List<ForeignKeyInfo> relacionamentos = schemaReaderOrigin.listarRelacionamentos();

        for (ForeignKeyInfo fk : relacionamentos)
        {
            try (Statement stmt = destinoConn.createStatement())
            {
                if (!colunaEhUnicaOuPK(destinoConn, fk.tabelaReferencia(), fk.colunaReferencia())) {
                    logger.warning("Ignorando FK para coluna não única: " + fk);
                    continue;
                }

                String constraintName = String.format(
                        "fk_%s_%s",
                        fk.tabelaOrigem().tableName(),
                        fk.colunaOrigem()
                ).replaceAll("[^a-zA-Z0-9_]", "_");

                String alter = String.format(
                        "ALTER TABLE \"%s\".\"%s\" ADD CONSTRAINT \"%s\" FOREIGN KEY (\"%s\") REFERENCES \"%s\".\"%s\"(\"%s\")",
                        fk.tabelaOrigem().schema(),
                        fk.tabelaOrigem().tableName(),
                        constraintName,
                        fk.colunaOrigem(),
                        fk.tabelaReferencia().schema(),
                        fk.tabelaReferencia().tableName(),
                        fk.colunaReferencia()
                );

                stmt.execute(alter);
                logger.info("Relacionamento adicionado: " + alter);
            }
            catch (SQLException e)
            {
                throw new RuntimeException("Erro ao adicionar relacionamento\n" + e.getMessage(), e);
            }
        }
    }

}