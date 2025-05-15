package cat.uvic.teknos.dam.miruvic.jdbc.datasources;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class SingleConnection implements DataSource {
    private Connection connection;
    private final String driver;
    private final String server;
    private final String database;
    private final String user;
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
        user = properties.getProperty("user");
        password = properties.getProperty("password");
    }

    @Override    
    public Connection getConnection(){        
        if (connection == null) {            
            try {                
                connection = DriverManager.getConnection(String.format("jdbc:%s://%s/%s",driver,server,database),user,password);            
            } catch (SQLException e) {               
                throw new RuntimeException(e);            
            }        
        }        
        return connection;    
    }
}