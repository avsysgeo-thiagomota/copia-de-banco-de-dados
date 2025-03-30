package com.vms.db.connector.postgres;

import com.vms.db.connector.ConnectionFactory;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class PostgresConnectionFactory implements ConnectionFactory {

    private final String url;
    private final String user;
    private final String password;

    public PostgresConnectionFactory(String configPath) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Properties props = new Properties();
        try (InputStream resourceStream = loader.getResourceAsStream(configPath))
        {
            props.load(resourceStream);
        }
        catch (IOException e)
        {
            throw new RuntimeException("Erro ao ler config do PostgreSQL: " + e.getMessage(), e);
        }

        String host = props.getProperty("db.url");
        String port = props.getProperty("db.port");
        String banco = props.getProperty("db.banco");
        this.user = props.getProperty("db.user");
        this.password = props.getProperty("db.password");

        this.url = "jdbc:postgresql://" + host + ":" + port + "/" + banco;
    }

    @Override
    public Connection createConnection() {
        try {
            return DriverManager.getConnection(url, user, password);
        }
        catch (SQLException e)
        {
            String mensagemErro = String.format(
                "Erro ao conectar no PostgreSQL (%s, %s, %s)",
                url,
                user,
                password);
            throw new RuntimeException(mensagemErro, e);
        }
    }
}
