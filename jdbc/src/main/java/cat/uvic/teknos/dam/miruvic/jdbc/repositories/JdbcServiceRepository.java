package cat.uvic.teknos.dam.miruvic.jdbc.repositories;

import java.sql.*;
import java.util.*;
import java.io.*;
import cat.uvic.teknos.dam.miruvic.model.Service;
import cat.uvic.teknos.dam.miruvic.model.impl.ServiceImpl;
import cat.uvic.teknos.dam.miruvic.repositories.ServiceRepository;
import cat.uvic.teknos.dam.miruvic.jdbc.exceptions.*;

public class JdbcServiceRepository implements ServiceRepository<Service> {
    private Connection getConnection() throws DataSourceException {
        var properties = new Properties();
        try {
            properties.load(new FileInputStream("datasource.properties"));
        } catch (IOException e) {
            throw new DataSourceException("Error al cargar el archivo de propiedades", e);
        }
        String driver = properties.getProperty("driver");
        String server = properties.getProperty("server");
        String database = properties.getProperty("database");
        String username = properties.getProperty("username");
        String password = properties.getProperty("password");
        try {
            return DriverManager.getConnection(String.format("jdbc:%s://%s/%s", driver, server, database),
                    username, password);
        } catch (SQLException e) {
            throw new DataSourceException("Error al conectar con la base de datos", e);
        }
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
                stmt.setInt(4, service.getId());
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
            throw new RepositoryException("Error al guardar el servicio", e);
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
            throw new RepositoryException("Error al eliminar el servicio", e);
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
                    return mapResultSetToService(rs);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error al obtener el servicio", e);
        }
        throw new EntityNotFoundException("Servicio no encontrado con el id:" + id);
    }



    @Override
    public Service findByName(String name) {
        String sql = "SELECT * FROM SERVICE WHERE name = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToService(rs);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error al buscar servicio por nombre", e);
        }
        throw new EntityNotFoundException("Servicio con nombre '" + name + "' no encontrado");
    }

    @Override
    public Set<Service> getAll() {
        Set<Service> services = new HashSet<>();
        String sql = "SELECT * FROM SERVICE";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                services.add(mapResultSetToService(rs));
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error al obtener todos los servicios", e);
        }
        return services;
    }
    
    @Override
    public Service findByType(String type) {
        String sql = "SELECT * FROM SERVICE WHERE type = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, type);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToService(rs);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error al buscar servicio por tipo", e);
        }
        throw new EntityNotFoundException("Servicio con tipo '" + type + "' no encontrado");
    }
    
    private Service mapResultSetToService(ResultSet rs) {
        try {
            Service service = new ServiceImpl();
            service.setId(rs.getInt("id"));
            service.setServiceName(rs.getString("name"));
            service.setDescription(rs.getString("description"));
            service.setPrice(rs.getBigDecimal("price"));
            return service;
        } catch (SQLException e) {
            throw new RepositoryException("Error al mapear los datos del servicio", e);
        }
    }
}