package com.vms;

import com.vms.db.connector.DatabaseConnector;
import com.vms.db.connector.postgres.PostgresConnectionFactory;
import com.vms.db.migrator.DatabaseMigrator;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.function.Supplier;

public class Main {

    private static DatabaseConnector origem;
    private static DatabaseConnector destino;

    public static void main(String[] args) {

        origem = new DatabaseConnector(
                new PostgresConnectionFactory("origem.properties")::createConnection
        );

        destino = new DatabaseConnector(
                new PostgresConnectionFactory("destino.properties")::createConnection
        );

        System.out.println("Bancos conectados com sucesso!");

//        try
//        {
//            DatabaseMigrator migrator = new DatabaseMigrator(origem.getConnection(), destino.getConnection());
//            migrator.migrarTudo();
//        }
//        catch (SQLException e)
//        {
//            throw new RuntimeException("Erro ao executar a migração de banco de dados\n" + e.getMessage(), e);
//        }
    }
}
