package cat.uvic.teknos.dam.miruvic.jdbc;

import java.sql.*;
import java.util.*;
import cat.uvic.teknos.dam.miruvic.Service;
import cat.uvic.teknos.dam.miruvic.impl.ServiceImpl;
import cat.uvic.teknos.dam.miruvic.ServiceRepository;

public class JdbcServiceRepository implements ServiceRepository {
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/RUVIC", "root", "rootpassword");
    }

    @Override
    public Service findByName(String name) {
        String sql = "SELECT * FROM SERVICE WHERE service_name = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Service service = new Service();
                    service.setId(rs.getInt("id"));
                    service.setServiceName(rs.getString("service_name"));
                    service.setDescription(rs.getString("description"));
                    service.setPrice(rs.getBigDecimal("price"));
                    return service;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public java.util.List<Service> findByType(String type) {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT * FROM SERVICE WHERE type = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, type);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Service service = new Service();
                    service.setId(rs.getInt("id"));
                    service.setServiceName(rs.getString("service_name"));
                    service.setDescription(rs.getString("description"));
                    service.setPrice(rs.getBigDecimal("price"));
                    services.add(service);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return services;
    }
}