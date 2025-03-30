package com.vms.db.connector;

import java.sql.Connection;

public class DatabaseConnector {

    private final ConnectionFactory factory;

    public DatabaseConnector(ConnectionFactory factory) {
        this.factory = factory;
    }

    public Connection getConnection() {
        return factory.createConnection();
    }
}
