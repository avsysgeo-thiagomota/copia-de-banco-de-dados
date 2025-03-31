package com.vms.db.connector;

import java.sql.Connection;
import java.util.function.Supplier;

public class DatabaseConnector {

    private final Supplier<Connection> connectionSupplier;

    public DatabaseConnector(Supplier<Connection> connectionSupplier) {
        this.connectionSupplier = connectionSupplier;
    }

    public Connection getConnection() {
        return connectionSupplier.get();
    }
}
