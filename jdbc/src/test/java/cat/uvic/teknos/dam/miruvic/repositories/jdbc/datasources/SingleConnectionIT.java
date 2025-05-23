package cat.uvic.teknos.dam.miruvic.repositories.jdbc.datasources;

import cat.uvic.teknos.dam.miruvic.jdbc.datasources.SingleConnectionDataSource;
import cat.uvic.teknos.dam.miruvic.jdbc.exceptions.DataSourceException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class SingleConnectionIT {

    @Test
    void getDriver() {
        var dataSource = new SingleConnectionDataSource();

        assertEquals("mysql", dataSource.getDriver());
    }

    @Test
    void getServer() {
    }

    @Test
    void getDatabase() {
    }

    @Test
    void getUser() {
    }

    @Test
    void getPassword() {
    }

    @Test
    void getConnectionOk() {
        var dataSource = new SingleConnectionDataSource();

        var connection = dataSource.getConnection();

        assertNotNull(connection);
    }

    @Test
    void getConnectionKo() {
        var dataSource = new SingleConnectionDataSource(
                "mysql",
                "localhost:3306",
                "RUVIC",
                "root",
                "rootpassword");

        assertThrows(DataSourceException.class, dataSource::getConnection);
    }
}