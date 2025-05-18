package cat.uvic.teknos.dam.miruvic.jdbc.datasources;

import java.sql.Connection;

public interface DataSource {
    Connection getConnection();
}