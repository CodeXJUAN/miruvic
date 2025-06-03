package cat.uvic.teknos.dam.miruvic.jdbc.repositories;

import cat.uvic.teknos.dam.miruvic.jdbc.datasources.DataSource;
import cat.uvic.teknos.dam.miruvic.model.Reservation;
import cat.uvic.teknos.dam.miruvic.model.Service;
import cat.uvic.teknos.dam.miruvic.model.ReservationService;
import cat.uvic.teknos.dam.miruvic.model.impl.ReservationImpl;
import cat.uvic.teknos.dam.miruvic.model.impl.ServiceImpl;
import cat.uvic.teknos.dam.miruvic.jdbc.models.JdbcReservationService;
import cat.uvic.teknos.dam.miruvic.repositories.ReservationServiceRepository;
import cat.uvic.teknos.dam.miruvic.jdbc.exceptions.RepositoryException;
import cat.uvic.teknos.dam.miruvic.jdbc.exceptions.EntityNotFoundException;

import java.sql.*;
import java.util.*;

public class JdbcReservationServiceRepository implements ReservationServiceRepository {
    private final DataSource dataSource;

    public JdbcReservationServiceRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void save(ReservationService reservationService) {
        Integer reservationId = reservationService.getReservation().stream().findFirst().map(Reservation::getId).orElse(null);
        Integer serviceId = reservationService.getService().stream().findFirst().map(Service::getId).orElse(null);
        Integer quantity = reservationService.getQuantity();

        if (reservationId == null || serviceId == null) {
            throw new RepositoryException("reservation_id y service_id no pueden ser nulos");
        }

        String selectSql = "SELECT COUNT(*) FROM RESERVATION_SERVICE WHERE reservation_id = ? AND service_id = ?";
        String insertSql = "INSERT INTO RESERVATION_SERVICE (reservation_id, service_id, quantity) VALUES (?, ?, ?)";
        String updateSql = "UPDATE RESERVATION_SERVICE SET quantity = ? WHERE reservation_id = ? AND service_id = ?";

        try (Connection conn = dataSource.getConnection()) {
            boolean exists;
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                selectStmt.setInt(1, reservationId);
                selectStmt.setInt(2, serviceId);
                try (ResultSet rs = selectStmt.executeQuery()) {
                    rs.next();
                    exists = rs.getInt(1) > 0;
                }
            }

            if (exists) {
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setInt(1, quantity);
                    updateStmt.setInt(2, reservationId);
                    updateStmt.setInt(3, serviceId);
                    updateStmt.executeUpdate();
                }
            } else {
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setInt(1, reservationId);
                    insertStmt.setInt(2, serviceId);
                    insertStmt.setInt(3, quantity);
                    insertStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error al guardar ReservationService", e);
        }
    }

    @Override
    public void delete(ReservationService reservationService) {
        Integer reservationId = reservationService.getReservation().stream().findFirst().map(Reservation::getId).orElse(null);
        Integer serviceId = reservationService.getService().stream().findFirst().map(Service::getId).orElse(null);

        if (reservationId == null || serviceId == null) {
            throw new RepositoryException("reservation_id y service_id no pueden ser nulos");
        }

        String sql = "DELETE FROM RESERVATION_SERVICE WHERE reservation_id = ? AND service_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, reservationId);
            stmt.setInt(2, serviceId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Error al eliminar ReservationService", e);
        }
    }

    @Override
    public ReservationService get(Integer id) {
        String sql = "SELECT * FROM RESERVATION_SERVICE WHERE reservation_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToReservationService(rs);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error al obtener ReservationService", e);
        }
        throw new EntityNotFoundException("ReservationService no encontrado con reservation_id: " + id);
    }

    @Override
    public Set<ReservationService> getAll() {
        Set<ReservationService> result = new HashSet<>();
        String sql = "SELECT * FROM RESERVATION_SERVICE";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                result.add(mapResultSetToReservationService(rs));
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error al obtener todos los ReservationService", e);
        }
        return result;
    }

    @Override
    public List<ReservationService> findByReservationId(Integer reservationId) {
        List<ReservationService> result = new ArrayList<>();
        String sql = "SELECT * FROM RESERVATION_SERVICE WHERE reservation_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, reservationId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(mapResultSetToReservationService(rs));
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error al buscar por reservation_id", e);
        }
        return result;
    }

    @Override
    public List<ReservationService> findByServiceId(Integer serviceId) {
        List<ReservationService> result = new ArrayList<>();
        String sql = "SELECT * FROM RESERVATION_SERVICE WHERE service_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, serviceId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(mapResultSetToReservationService(rs));
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error al buscar por service_id", e);
        }
        return result;
    }

    private ReservationService mapResultSetToReservationService(ResultSet rs) throws SQLException {
        JdbcReservationService reservationService = new JdbcReservationService();

        ReservationImpl reservation = new ReservationImpl();
        reservation.setId(rs.getInt("reservation_id"));
        reservationService.setReservation(Set.of(reservation));

        ServiceImpl service = new ServiceImpl();
        service.setId(rs.getInt("service_id"));
        reservationService.setService(Set.of(service));

        reservationService.setQuantity(rs.getInt("quantity"));

        return reservationService;
    }
}