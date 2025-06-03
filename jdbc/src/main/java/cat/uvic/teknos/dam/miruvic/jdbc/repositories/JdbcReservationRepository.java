package cat.uvic.teknos.dam.miruvic.jdbc.repositories;

import java.sql.*;
import java.util.*;
import java.time.*;
import cat.uvic.teknos.dam.miruvic.model.Reservation;
import cat.uvic.teknos.dam.miruvic.model.Student;
import cat.uvic.teknos.dam.miruvic.model.Room;
import cat.uvic.teknos.dam.miruvic.model.impl.ReservationImpl;
import cat.uvic.teknos.dam.miruvic.model.impl.StudentImpl;
import cat.uvic.teknos.dam.miruvic.model.impl.RoomImpl;
import cat.uvic.teknos.dam.miruvic.model.impl.AddressImpl;
import cat.uvic.teknos.dam.miruvic.repositories.ReservationRepository;
import cat.uvic.teknos.dam.miruvic.jdbc.exceptions.*;
import cat.uvic.teknos.dam.miruvic.jdbc.datasources.DataSource;

public class JdbcReservationRepository implements ReservationRepository {

    private final DataSource dataSource;

    public JdbcReservationRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void save(Reservation reservation) {
        String sql;
        if (reservation.getId() == null || reservation.getId() == 0) {
            sql = "INSERT INTO RESERVATION (student_id, room_id, start_date, end_date, status) VALUES (?, ?, ?, ?, ?)";
        } else {
            sql = "UPDATE RESERVATION SET student_id = ?, room_id = ?, start_date = ?, end_date = ?, status = ? WHERE id = ?";
        }

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            Student student = reservation.getStudent().stream().findFirst().orElse(null);
            Room room = reservation.getRoom().stream().findFirst().orElse(null);

            if (student == null || room == null) {
                throw new RepositoryException("La reserva debe tener al menos un estudiante y una habitación");
            }

            stmt.setInt(1, student.getId());
            stmt.setInt(2, room.getId());
            stmt.setDate(3, java.sql.Date.valueOf(reservation.getStartDate()));
            stmt.setDate(4, java.sql.Date.valueOf(reservation.getEndDate()));
            stmt.setString(5, reservation.getStatus());

            if (reservation.getId() != null && reservation.getId() != 0) {
                stmt.setInt(6, reservation.getId());
            }

            stmt.executeUpdate();

            if (reservation.getId() == null || reservation.getId() == 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        reservation.setId(generatedKeys.getInt(1));
                    }
                }
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error al guardar la reserva", e);
        }
    }

    @Override
    public void delete(Reservation reservation) {
        String deleteReservationSql = "DELETE FROM RESERVATION WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement deleteStmt = conn.prepareStatement(deleteReservationSql)) {
            deleteStmt.setInt(1, reservation.getId());
            deleteStmt.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Error al eliminar la reserva", e);
        }
    }

    @Override
    public Reservation get(Integer id) {
        String sql = "SELECT r.*, s.id as student_id, s.first_name as student_name, s.last_name as student_surname, " +
                "s.email as student_email, s.phone_number as student_phone, s.address_id as student_address_id, " +
                "a.id as address_id, a.street, a.city, a.state, a.zip_code, a.country, " +
                "rm.id_room as room_id, rm.room_number, rm.floor, rm.capacity, rm.room_type, rm.price as room_price " +
                "FROM RESERVATION r " +
                "JOIN STUDENT s ON r.student_id = s.id " +
                "LEFT JOIN ADDRESS a ON s.address_id = a.id " +
                "JOIN ROOM rm ON r.room_id = rm.id_room " +
                "WHERE r.id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToReservation(rs);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error al obtener la reserva", e);
        }
        throw new EntityNotFoundException("Reservation not found with id: " + id);
    }

    @Override
    public Set<Reservation> getAll() {
        Set<Reservation> reservations = new HashSet<>();
        String sql = "SELECT r.*, s.id as student_id, s.first_name as student_name, s.last_name as student_surname, " +
                "s.email as student_email, s.phone_number as student_phone, s.address_id as student_address_id, " +
                "a.id as address_id, a.street, a.city, a.state, a.zip_code, a.country, " +
                "rm.id_room as room_id, rm.room_number, rm.floor, rm.capacity, rm.room_type, rm.price as room_price " +
                "FROM RESERVATION r " +
                "JOIN STUDENT s ON r.student_id = s.id " +
                "LEFT JOIN ADDRESS a ON s.address_id = a.id " +
                "JOIN ROOM rm ON r.room_id = rm.id_room";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Reservation reservation = mapResultSetToReservation(rs);
                reservations.add(reservation);
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error al obtener todas las reservas", e);
        }
        return reservations;
    }

    @Override
    public List<Reservation> findByDate(LocalDate date) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT r.*, s.id as student_id, s.first_name as student_name, s.last_name as student_surname, " +
                "s.email as student_email, s.phone_number as student_phone, s.address_id as student_address_id, " +
                "a.id as address_id, a.street, a.city, a.state, a.zip_code, a.country, " +
                "rm.id_room as room_id, rm.room_number, rm.floor, rm.capacity, rm.room_type, rm.price as room_price " +
                "FROM RESERVATION r " +
                "JOIN STUDENT s ON r.student_id = s.id " +
                "LEFT JOIN ADDRESS a ON s.address_id = a.id " +
                "JOIN ROOM rm ON r.room_id = rm.id_room " +
                "WHERE r.start_date <= ? AND r.end_date >= ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, java.sql.Date.valueOf(date));
            stmt.setDate(2, java.sql.Date.valueOf(date));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Reservation reservation = mapResultSetToReservation(rs);
                    reservations.add(reservation);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error al buscar reservas por fecha", e);
        }
        return reservations;
    }

    @Override
    public List<Reservation> findByStudentId(Integer studentId) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT r.*, s.id as student_id, s.first_name as student_name, s.last_name as student_surname, " +
                "s.email as student_email, s.phone_number as student_phone, s.address_id as student_address_id, " +
                "a.id as address_id, a.street, a.city, a.state, a.zip_code, a.country, " +
                "rm.id_room as room_id, rm.room_number, rm.floor, rm.capacity, rm.room_type, rm.price as room_price " +
                "FROM RESERVATION r " +
                "JOIN STUDENT s ON r.student_id = s.id " +
                "LEFT JOIN ADDRESS a ON s.address_id = a.id " +
                "JOIN ROOM rm ON r.room_id = rm.id_room " +
                "WHERE r.student_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Reservation reservation = mapResultSetToReservation(rs);
                    reservations.add(reservation);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error al buscar reservas por ID de estudiante", e);
        }

        return reservations;
    }

    private Reservation mapResultSetToReservation(ResultSet rs) throws SQLException {
        Reservation reservation = new ReservationImpl();
        reservation.setId(rs.getInt("id"));
        reservation.setStartDate(rs.getDate("start_date").toLocalDate());
        reservation.setEndDate(rs.getDate("end_date").toLocalDate());
        reservation.setStatus(rs.getString("status"));

        StudentImpl student = new StudentImpl();
        student.setId(rs.getInt("student_id"));
        student.setFirstName(rs.getString("student_name"));
        student.setLastName(rs.getString("student_surname"));
        student.setEmail(rs.getString("student_email"));
        student.setPhoneNumber(rs.getString("student_phone"));

        int addressId = rs.getInt("address_id");
        if (!rs.wasNull() && addressId != 0) {
            AddressImpl address = new AddressImpl();
            address.setId(addressId);
            address.setStreet(rs.getString("street"));
            address.setCity(rs.getString("city"));
            address.setState(rs.getString("state"));
            address.setZipCode(rs.getString("zip_code"));
            address.setCountry(rs.getString("country"));
            student.setAddress(Set.of(address));
        }

        reservation.setStudent(Set.of(student));

        RoomImpl room = new RoomImpl();
        room.setId(rs.getInt("room_id"));
        room.setRoomNumber(rs.getString("room_number"));
        room.setFloor(rs.getInt("floor"));
        room.setCapacity(rs.getInt("capacity"));
        room.setType(rs.getString("room_type"));
        room.setPrice(rs.getBigDecimal("room_price"));
        reservation.setRoom(Set.of(room));

        return reservation;
    }
}