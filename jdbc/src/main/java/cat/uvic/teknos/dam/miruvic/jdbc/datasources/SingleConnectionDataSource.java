package cat.uvic.teknos.dam.miruvic.jdbc.datasources;

import java.sql.*;
import java.util.*;
import java.io.*;
import cat.uvic.teknos.dam.miruvic.jdbc.exceptions.DataSourceException;

public class SingleConnectionDataSource implements DataSource {
    private Connection connection;
    private final String driver;
    private final String server;
    private final String database;
    private final String user;
    private final String password;

    public SingleConnectionDataSource(String driver, String server, String database, String user, String password) {
        this.driver = driver;
        this.server = server;
        this.database = database;
        this.user = user;
        this.password = password;
    }

    public SingleConnectionDataSource() {
        var properties = new Properties();

        try {
            properties.load(this.getClass().getResourceAsStream("/datasource.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        driver = properties.getProperty("driver");
        server = properties.getProperty("server");
        database = properties.getProperty("database");
        user = properties.getProperty("user");
        password = properties.getProperty("password");
    }

    @Override
    public Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(String.format("jdbc:%s://%s/%s", driver, server, database),
                        user,
                        password);
            } catch (SQLException e) {
                throw new DataSourceException("Invalid parameters", e);
            }
        }

        return connection;
    }

    public String getDriver() {
        return driver;
    }

    public String getServer() {
        return server;
    }

    public String getDatabase() {
        return database;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }
}