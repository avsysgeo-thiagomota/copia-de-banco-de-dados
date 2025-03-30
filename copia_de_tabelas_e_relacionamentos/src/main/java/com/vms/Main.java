package com.vms;

import com.vms.db.connector.DatabaseConnector;
import com.vms.db.connector.postgres.PostgresConnectionFactory;
import com.vms.db.migrator.DatabaseMigrator;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class Main {
    private static ResourceBundle rb = ResourceBundle.getBundle("origem");

    public static void main(String[] args) {

        DatabaseConnector origem = new DatabaseConnector(
                new PostgresConnectionFactory("origem.properties")
        );
        DatabaseConnector destino = new DatabaseConnector(
                new PostgresConnectionFactory("destino.properties")
        );

        try (Connection connectionOrigem = origem.getConnection();
             Connection connectionDestino = destino.getConnection();)
        {
            System.out.println("Conectado com sucesso!");
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Erro ao se connercar aos bancos de dados\n" + e.getMessage(), e);
        }

        try
        {
            DatabaseMigrator migrator = new DatabaseMigrator(origem.getConnection(), destino.getConnection());
            migrator.migrarTudo();
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Erro ao executar a migração de banco de dados\n" + e.getMessage(), e);
        }
    }
}
