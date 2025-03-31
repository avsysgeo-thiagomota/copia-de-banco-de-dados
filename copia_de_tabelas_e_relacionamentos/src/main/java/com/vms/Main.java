package com.vms;

import com.vms.db.connector.DatabaseConnector;
import com.vms.db.connector.postgres.PostgresConnectionFactory;
import com.vms.db.migrator.DatabaseMigrator;
import com.vms.db.reader.SchemaReader;
import com.vms.util.LogConfig;

public class Main {

    public static void main(String[] args) {

        // Inicializa configuração de logging (arquivo + console)
        LogConfig.configurar();
        DatabaseConnector origem = new DatabaseConnector(
                    new PostgresConnectionFactory("origem.properties")::createConnection
            );
        SchemaReader schemaReader = new SchemaReader(origem.getConnection());
        System.out.println(schemaReader.listarTabelas());

//        try {
//            // Conecta ao banco de origem usando propriedades externas
//            DatabaseConnector origem = new DatabaseConnector(
//                    new PostgresConnectionFactory("origem.properties")::createConnection
//            );
//
//            // Conecta ao banco de destino usando propriedades externas
//            DatabaseConnector destino = new DatabaseConnector(
//                    new PostgresConnectionFactory("destino.properties")::createConnection
//            );
//
//            System.out.println("Bancos conectados com sucesso!");
//
//            // Cria migrador e executa migração completa
//            DatabaseMigrator migrator = new DatabaseMigrator(origem.getConnection(), destino.getConnection());
//            migrator.migrarTudo();
//
//        } catch (Exception e) {
//            // Captura qualquer erro durante o processo
//            throw new RuntimeException("Erro ao executar a migração de banco de dados\n" + e.getMessage(), e);
//        }
    }
}