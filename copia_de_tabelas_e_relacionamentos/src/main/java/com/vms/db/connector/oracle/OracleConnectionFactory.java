package com.vms.db.connector.oracle;

import com.vms.db.connector.ConnectionFactory;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class OracleConnectionFactory implements ConnectionFactory {

    private final String url;
    private final String user;
    private final String password;

    public OracleConnectionFactory(String configPath) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Properties props = new Properties();
        try (InputStream resourceStream = loader.getResourceAsStream(configPath))
        {
            props.load(resourceStream);
        }
        catch (IOException e)
        {
            throw new RuntimeException("Erro ao ler arquivo de configuração Oracle: " + e.getMessage(), e);
        }

        String host = props.getProperty("db.url");
        String port = props.getProperty("db.port");
        this.user = props.getProperty("db.user");
        this.password = props.getProperty("db.password");

        if (props.containsKey("db.service"))
        {
            String service = props.getProperty("db.service");
            this.url = "jdbc:oracle:thin:@//" + host + ":" + port + "/" + service;
        }
        else if (props.containsKey("db.sid"))
        {
            String sid = props.getProperty("db.sid");
            this.url = "jdbc:oracle:thin:@" + host + ":" + port + ":" + sid;
        }
        else
        {
            throw new IllegalArgumentException("Configuração inválida: é necessário fornecer db.service ou db.sid");
        }
    }

    @Override
    public Connection createConnection() {
        try
        {
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
