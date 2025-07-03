package cat.uvic.teknos.dam.miruvic.repositories.jdbc.datasources;

import cat.uvic.teknos.dam.miruvic.jdbc.datasources.SingleConnectionDataSource;
import cat.uvic.teknos.dam.miruvic.jdbc.exceptions.DataSourceException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
class SingleConnectionIT {

    private static SingleConnectionDataSource dataSource;

    @BeforeAll
    static void setup() {
        dataSource = new SingleConnectionDataSource(
                "mysql",
                "localhost:3306",
                "miruvic_test",
                "juanm",
                "teknos"
        );
    }

    @Test
    void getConnection_NotNull() {
        Connection conn = dataSource.getConnection();
        assertNotNull(conn, "Connection should not be null");
    }

    @Test
    void getConnection_IsValid() throws SQLException {
        Connection conn = dataSource.getConnection();
        assertTrue(conn.isValid(2), "Connection should be valid");
    }

    @Test
    void getSameConnectionInstance() {
        Connection conn1 = dataSource.getConnection();
        Connection conn2 = dataSource.getConnection();
        assertSame(conn1, conn2, "Should return the same connection instance");
    }
}