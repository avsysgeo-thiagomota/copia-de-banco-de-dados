package com.vms.db.migrator;

import com.vms.db.reader.DataReader;
import com.vms.db.reader.SchemaReader;
import com.vms.db.writer.DataWriter;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

public class DatabaseMigrator {
    private final SchemaReader schemaReader;
    private final DataReader dataReader;
    private final DataWriter dataWriter;

    // Construtor principal: injeta conexões de origem e destino
    public DatabaseMigrator(Connection origem, Connection destino) {
        this.schemaReader = new SchemaReader(origem);
        this.dataReader = new DataReader(origem);
        this.dataWriter = new DataWriter(destino);
    }

    // Construtor alternativo: injeta dependências diretamente (bom para testes)
    public DatabaseMigrator(SchemaReader schemaReader, DataReader dataReader, DataWriter dataWriter) {
        this.schemaReader = schemaReader;
        this.dataReader = dataReader;
        this.dataWriter = dataWriter;
    }

    /**
     * Executa a migração completa: lê todas as tabelas da origem e insere os dados no destino.
     * Se uma tabela falhar, a migração das demais continua.
     */
    public void migrarTudo() {
        try {
            List<String> tabelas = schemaReader.listarTabelas();

            for (String tabela : tabelas) {
                try {
                    List<Map<String, Object>> dados = dataReader.lerDados(tabela);
                    dataWriter.inserirDados(tabela, dados);
                    System.out.println("Migrada: " + tabela + " (" + dados.size() + " linhas)");
                } catch (Exception exTabela) {
                    System.err.println("Erro ao migrar tabela '" + tabela + "': " + exTabela.getMessage());
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao executar a migração de banco de dados\n" + e.getMessage(), e);
        }
    }
}