package cat.uvic.teknos.dam.miruvic.jdbc.datasources;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class HikariDataSourceWrapper implements DataSource {
    private final HikariDataSource ds;

    public HikariDataSourceWrapper() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("datasource.properties")) {
            if (input == null) {
                throw new RuntimeException("No se encontró el archivo datasource.properties");
            }
            props.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Error cargando datasource.properties", e);
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:" + props.getProperty("driver") + "://" + props.getProperty("server") + "/" + props.getProperty("database"));
        config.setUsername(props.getProperty("user"));
        config.setPassword(props.getProperty("password"));
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(5);
        config.setIdleTimeout(300000);
        config.setConnectionTimeout(20000);
        config.setAutoCommit(true);

        ds = new HikariDataSource(config);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    @Override
    public void close() {
        if (ds != null && !ds.isClosed()) {
            ds.close();
        }
    }
}
