package com.vms;

import com.vms.db.connector.DatabaseConnector;
import com.vms.db.migrator.DatabaseMigrator;

public class Main {
    public static void main(String[] args) {
        DatabaseConnector origem = new DatabaseConnector("src/config/origem.properties");
        DatabaseConnector destino = new DatabaseConnector("src/config/destino.properties");

        try {
            DatabaseMigrator migrator = new DatabaseMigrator(origem.getConnection(), destino.getConnection());
            migrator.migrarTudo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
