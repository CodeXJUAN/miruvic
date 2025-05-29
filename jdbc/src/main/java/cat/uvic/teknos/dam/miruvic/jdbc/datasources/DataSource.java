package cat.uvic.teknos.dam.miruvic.jdbc.datasources;

import java.sql.Connection;
import java.sql.SQLException;

public interface DataSource {
    Connection getConnection() throws SQLException;
}