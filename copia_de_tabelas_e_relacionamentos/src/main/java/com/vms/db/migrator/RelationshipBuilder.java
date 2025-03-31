package com.vms.db.migrator;

import com.vms.db.model.ForeignKeyInfo;
import com.vms.db.model.SchemaAndTable;
import com.vms.db.reader.SchemaReader;

import java.sql.*;
import java.util.List;
import java.util.logging.Logger;

public class RelationshipBuilder {

    private static final Logger logger = Logger.getLogger(RelationshipBuilder.class.getName());
    public Connection origemConn;
    public Connection destinoConn;

    public RelationshipBuilder(Connection origemConn, Connection destinoConn)
    {
        this.origemConn = origemConn;
        this.destinoConn = destinoConn;
    }

    /**
     * Verifica se uma coluna é PRIMARY KEY ou possui índice UNIQUE.
     */
    private boolean colunaEhUnicaOuPK(Connection conn, SchemaAndTable tabela, String coluna)
    {

        try
        {
            DatabaseMetaData meta = conn.getMetaData();

            // Verifica se a coluna faz parte da PRIMARY KEY
            try (ResultSet rsPk = meta.getPrimaryKeys(null, tabela.schema(), tabela.tableName()))
            {
                while (rsPk.next())
                {
                    if (coluna.equalsIgnoreCase(rsPk.getString("COLUMN_NAME")))
                    {
                        return true;
                    }
                }
            }
            catch (SQLException e)
            {
                throw new RuntimeException("Erro ao verificar se a coluna faz parte da PRIMARY KEY\n" + e.getMessage(), e);
            }

            // Verifica se existe índice UNIQUE para a coluna
            try (ResultSet rsUnique = meta.getIndexInfo(null, tabela.schema(), tabela.tableName(), true, false))
            {
                while (rsUnique.next())
                {
                    if (coluna.equalsIgnoreCase(rsUnique.getString("COLUMN_NAME")))
                    {
                        return true;
                    }
                }
            }
            catch (SQLException e)
            {
                throw new RuntimeException("Erro ao verificar se existe índice UNIQUE para a coluna\n" + e.getMessage(), e);
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Erro ao obter metadados da tabela\n" + e.getMessage(), e);
        }

        return false;
    }

    /**
     * Cria constraints de chave estrangeira em tabelas clonadas com base nos relacionamentos detectados.
     * Apenas adiciona FKs se a coluna de referência for UNIQUE ou PK.
     */
    public void criarRelacionamentos(SchemaReader schemaReaderOrigem)
    {
        List<ForeignKeyInfo> relacionamentos = schemaReaderOrigem.listarRelacionamentos();

        for (ForeignKeyInfo fk : relacionamentos)
        {
            try
            {
                // Verifica se a coluna referenciada no destino é única ou PK
                if (!colunaEhUnicaOuPK(destinoConn, fk.tabelaReferencia(), fk.colunaReferencia())) {
                    logger.warning("Ignorando FK para coluna não única: " + fk);
                    continue;
                }

                // Gera nome da constraint sanitizado
                String constraintName = gerarNomeConstraint(fk);

                // Comando ALTER TABLE para adicionar a constraint no banco de destino
                String alter = String.format(
                        "ALTER TABLE \"%s\".\"%s\" ADD CONSTRAINT \"%s\" " +
                                "FOREIGN KEY (\"%s\") REFERENCES \"%s\".\"%s\"(\"%s\")",
                        fk.tabelaOrigem().schema(),
                        fk.tabelaOrigem().tableName(),
                        constraintName,
                        fk.colunaOrigem(),
                        fk.tabelaReferencia().schema(),
                        fk.tabelaReferencia().tableName(),
                        fk.colunaReferencia()
                );

                try (Statement stmt = destinoConn.createStatement())
                {
                    stmt.execute(alter);
                    logger.info("Relacionamento adicionado com sucesso: " + alter);
                }

            }
            catch (SQLException e)
            {
                logger.severe("Relacionamento: " + fk + "\n" + e.getMessage());
                throw new RuntimeException("Erro ao adicionar relacionamento", e);
            }
        }
    }

    /**
     * Gera um nome de constraint de forma segura e sanitizada.
     */
    private String gerarNomeConstraint(ForeignKeyInfo fk) {
        String raw = String.format("fk_%s_%s", fk.tabelaOrigem().tableName(), fk.colunaOrigem());
        return raw.replaceAll("[^a-zA-Z0-9_]", "_").toLowerCase();
    }



}
