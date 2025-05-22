package cat.uvic.teknos.dam.miruvic.jdbc.repositories;

import java.sql.*;
import java.util.*;
import java.io.*;
import java.math.BigDecimal;
import cat.uvic.teknos.dam.miruvic.Service;
import cat.uvic.teknos.dam.miruvic.impl.ServiceImpl;
import cat.uvic.teknos.dam.miruvic.ServiceRepository;

public class JdbcServiceRepository implements ServiceRepository {
    private Connection getConnection() throws SQLException {
        var properties = new Properties();
        try {
            properties.load(new FileInputStream("datasource.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String driver = properties.getProperty("driver");
        String server = properties.getProperty("server");
        String database = properties.getProperty("database");
        String username = properties.getProperty("username");
        String password = properties.getProperty("password");
        return DriverManager.getConnection(String.format("jdbc:%s://%s/%s", driver, server, database),
                username, password);
    }

    @Override
    public void save(Service service) {
        String sql;
        if (service.getId() == 0) {
            sql = "INSERT INTO SERVICE (name, description, price) VALUES (?, ?, ?)";
        } else {
            sql = "UPDATE SERVICE SET name = ?, description = ?, price = ? WHERE id = ?";
        }
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, service.getServiceName());
            stmt.setString(2, service.getDescription());
            stmt.setBigDecimal(3, service.getPrice());
            
            if (service.getId() != 0) {
                stmt.setInt(5, service.getId());
            }
            
            stmt.executeUpdate();
            
            if (service.getId() == 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        service.setId(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving service", e);
        }
    }

    @Override
    public void delete(Service service) {
        String sql = "DELETE FROM SERVICE WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, service.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting service", e);
        }
    }

    @Override
    public Service get(Integer id) {
        String sql = "SELECT * FROM SERVICE WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Service service = new ServiceImpl();
                    service.setId(rs.getInt("id"));
                    service.setServiceName(rs.getString("name"));
                    service.setDescription(rs.getString("description"));
                    service.setPrice(rs.getBigDecimal("price"));
                    return service;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting service", e);
        }
        return null;
    }

    @Override
    public List<Service> getAll() {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT * FROM SERVICE";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Service service = new ServiceImpl();
                service.setId(rs.getInt("id"));
                service.setServiceName(rs.getString("name"));
                service.setDescription(rs.getString("description"));
                service.setPrice(rs.getBigDecimal("price"));
                services.add(service);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting all services", e);
        }
        return services;
    }

    @Override
    public Service findByName(String name) {
        String sql = "SELECT * FROM SERVICE WHERE name = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Service service = new ServiceImpl();
                    service.setId(rs.getInt("id"));
                    service.setServiceName(rs.getString("name"));
                    service.setDescription(rs.getString("description"));
                    service.setPrice(rs.getBigDecimal("price"));
                    return service;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding service by name", e);
        }
        return null;
    }

    @Override
    public Set<Service> getAll() {
        Set<Service> services = new HashSet<>();
        String sql = "SELECT * FROM SERVICE";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Service service = new ServiceImpl();
                service.setId(rs.getInt("id"));
                service.setServiceName(rs.getString("name"));
                service.setDescription(rs.getString("description"));
                service.setPrice(rs.getBigDecimal("price"));
                services.add(service);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting all services", e);
        }
        return services;
    }
}