package com.vms.db.connector;

import java.sql.Connection;

@FunctionalInterface
public interface ConnectionFactory {
    Connection createConnection();
}