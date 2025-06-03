package cat.uvic.teknos.dam.miruvic.jdbc.repositories;

import java.sql.*;
import java.util.*;

import cat.uvic.teknos.dam.miruvic.model.Service;
import cat.uvic.teknos.dam.miruvic.model.impl.ServiceImpl;
import cat.uvic.teknos.dam.miruvic.repositories.ServiceRepository;
import cat.uvic.teknos.dam.miruvic.jdbc.datasources.DataSource;
import cat.uvic.teknos.dam.miruvic.jdbc.exceptions.*;

public class JdbcServiceRepository implements ServiceRepository {

    private final DataSource dataSource;

    public JdbcServiceRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void save(Service service) {
        String sql;
        if (service.getId() == null || service.getId() == 0) {
            sql = "INSERT INTO SERVICE (name, description, price) VALUES (?, ?, ?)";
        } else {
            sql = "UPDATE SERVICE SET name = ?, description = ?, price = ? WHERE id = ?";
        }

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, service.getServiceName());
            stmt.setString(2, service.getDescription());
            stmt.setBigDecimal(3, service.getPrice());

            if (service.getId() != null && service.getId() != 0) {
                stmt.setInt(4, service.getId());
            }

            stmt.executeUpdate();

            if (service.getId() == null || service.getId() == 0) {
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
        String checkSql = "SELECT COUNT(*) FROM RESERVATION_SERVICE WHERE service_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setInt(1, service.getId());

            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    String deleteRelationSql = "DELETE FROM RESERVATION_SERVICE WHERE service_id = ?";
                    try (PreparedStatement deleteRelationStmt = conn.prepareStatement(deleteRelationSql)) {
                        deleteRelationStmt.setInt(1, service.getId());
                        deleteRelationStmt.executeUpdate();
                    }
                }
            }

            String deleteSql = "DELETE FROM SERVICE WHERE id = ?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setInt(1, service.getId());
                deleteStmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error al eliminar el servicio", e);
        }
    }

    @Override
    public Service get(Integer id) {
        String sql = "SELECT * FROM SERVICE WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
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
        throw new EntityNotFoundException("Servicio no encontrado con el id: " + id);
    }

    @Override
    public Set<Service> getAll() {
        Set<Service> services = new HashSet<>();
        String sql = "SELECT * FROM SERVICE";

        try (Connection conn = dataSource.getConnection();
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
    public List<Service> findByName(String name) {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT * FROM SERVICE WHERE name = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    services.add(mapResultSetToService(rs));
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error al buscar servicios por nombre", e);
        }
        return services;
    }

    @Override
    public List<Service> findByType(String type) {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT * FROM SERVICE WHERE type = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, type);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    services.add(mapResultSetToService(rs));
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error al buscar servicios por tipo", e);
        }
        return services;
    }

    private Service mapResultSetToService(ResultSet rs) throws SQLException {
        ServiceImpl service = new ServiceImpl();
        service.setId(rs.getInt("id"));
        service.setServiceName(rs.getString("name"));
        service.setDescription(rs.getString("description"));
        service.setPrice(rs.getBigDecimal("price"));
        return service;
    }
}