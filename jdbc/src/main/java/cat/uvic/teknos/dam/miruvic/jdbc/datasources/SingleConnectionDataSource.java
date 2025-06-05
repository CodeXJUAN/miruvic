package cat.uvic.teknos.dam.miruvic.jdbc.datasources;

import java.sql.*;
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

    @Override
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}