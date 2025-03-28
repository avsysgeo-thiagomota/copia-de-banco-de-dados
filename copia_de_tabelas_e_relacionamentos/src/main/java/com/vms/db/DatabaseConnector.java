package com.vms.db;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnector {

    private String Url;
    private String Port;
    private String Banco;
    private String User;
    private String Password;

    public DatabaseConnector(String path)
    {
        try(FileReader reader = new FileReader(path))
        {
            Properties configDB = new Properties();
            configDB.load(reader);

            this.Url        = configDB.getProperty("db.url");
            this.Port       = configDB.getProperty("db.port");
            this.Banco      = configDB.getProperty("db.banco");
            this.User       = configDB.getProperty("db.user");
            this.Password   = configDB.getProperty("db.password");
        }
        catch(FileNotFoundException e)
        {
            throw new RuntimeException("File not found: " + e.getMessage(), e);
        }
        catch (IOException e)
        {
            throw new RuntimeException("Erro ao ler arquivo de configuração: " + e.getMessage(), e);
        }
    }

    public Connection getConnection() {
        String connectionURL = "jdbc:postgresql://" + this.Url + ":" + this.Port + "/" + this.Banco;

        try
        {
            return DriverManager.getConnection(connectionURL, this.User, this.Password);
        }
        catch (SQLException e)
        {
            throw new RuntimeException("Erro na conexão: " + e.getMessage(), e);
        }
    }
}
