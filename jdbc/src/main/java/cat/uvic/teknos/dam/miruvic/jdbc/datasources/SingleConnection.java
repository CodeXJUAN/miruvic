package cat.uvic.teknos.dam.miruvic.jdbc.datasources;

import java.sql.*;
import java.util.*;
import java.io.FileInputStream;
import java.io.IOException;
import cat.uvic.teknos.dam.miruvic.datasources.DataSource;

public class SingleConnection implements DataSource {
    private Connection connection;
    private final String driver;
    private final String server;
    private final String database;
    private final String username;
    private final String password;

    public SingleConnection() {
        var properties = new Properties();
        try {
            properties.load(new FileInputStream("datasource.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        driver = properties.getProperty("driver");
        server = properties.getProperty("server");
        database = properties.getProperty("database");
        username = properties.getProperty("username");
        password = properties.getProperty("password");
    }

    @Override
    public Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(String.format("jdbc:%s://%s/%s", driver, server, database),
                        username, password);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return connection;
    }
}