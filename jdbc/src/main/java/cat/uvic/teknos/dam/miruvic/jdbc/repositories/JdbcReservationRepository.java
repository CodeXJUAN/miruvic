package cat.uvic.teknos.dam.miruvic.jdbc.repositories;

import java.sql.*;
import java.util.*;
import java.time.*;
import cat.uvic.teknos.dam.miruvic.model.Reservation;
import cat.uvic.teknos.dam.miruvic.model.Student;
import cat.uvic.teknos.dam.miruvic.model.Room;
import cat.uvic.teknos.dam.miruvic.model.Service;
import cat.uvic.teknos.dam.miruvic.model.impl.ReservationImpl;
import cat.uvic.teknos.dam.miruvic.model.impl.StudentImpl;
import cat.uvic.teknos.dam.miruvic.model.impl.RoomImpl;
import cat.uvic.teknos.dam.miruvic.model.impl.AddressImpl;
import cat.uvic.teknos.dam.miruvic.model.impl.ServiceImpl;
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
            
            // Asumimos que solo hay un estudiante y una habitación en la reserva
            Student student = reservation.getStudent().iterator().next();
            Room room = reservation.getRoom().iterator().next();
            
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
            
            // Guardar los servicios asociados a la reserva
            saveReservationServices(reservation);
            
        } catch (SQLException e) {
            throw new RepositoryException("Error al guardar la reserva", e);
        }
    }
    
    private void saveReservationServices(Reservation reservation) {
        // Primero eliminamos los servicios existentes para esta reserva
        String deleteSql = "DELETE FROM RESERVATION_SERVICE WHERE reservation_id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
            stmt.setInt(1, reservation.getId());
            stmt.executeUpdate();
            
            // Ahora insertamos los nuevos servicios
            if (reservation.getServices() != null && !reservation.getServices().isEmpty()) {
                String insertSql = "INSERT INTO RESERVATION_SERVICE (reservation_id, service_id) VALUES (?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    for (Service service : reservation.getServices()) {
                        insertStmt.setInt(1, reservation.getId());
                        insertStmt.setInt(2, service.getId());
                        insertStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error al guardar los servicios de la reserva", e);
        }
    }

    @Override
    public void delete(Reservation reservation) {
        // Primero eliminamos los servicios asociados
        String deleteServicesSql = "DELETE FROM RESERVATION_SERVICE WHERE reservation_id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteServicesSql)) {
            stmt.setInt(1, reservation.getId());
            stmt.executeUpdate();
            
            // Ahora eliminamos la reserva
            String deleteReservationSql = "DELETE FROM RESERVATION WHERE id = ?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteReservationSql)) {
                deleteStmt.setInt(1, reservation.getId());
                deleteStmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error al eliminar la reserva", e);
        }
    }

    @Override
    public Reservation get(Integer id) {
        String sql = "SELECT r.*, s.id as student_id, s.name as student_name, s.surname as student_surname, " +
                    "s.email as student_email, s.phone as student_phone, " +
                    "rm.id_room as room_id, rm.room_number, rm.floor, rm.capacity, rm.room_type, rm.price as room_price " +
                    "FROM RESERVATION r " +
                    "JOIN STUDENT s ON r.student_id = s.id " +
                    "JOIN ROOM rm ON r.room_id = rm.id_room " +
                    "WHERE r.id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Reservation reservation = mapResultSetToReservation(rs);
                    loadReservationServices(reservation);
                    return reservation;
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error al obtener la reserva", e);
        }
        throw new EntityNotFoundException("Reservation not found with id: " + id);
    }
    
    private void loadReservationServices(Reservation reservation) {
        String sql = "SELECT s.* FROM SERVICE s " +
                    "JOIN RESERVATION_SERVICE rs ON s.id = rs.service_id " +
                    "WHERE rs.reservation_id = ?";
                    
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, reservation.getId());
            
            Set<Service> services = new HashSet<>();
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Service service = new ServiceImpl();
                    service.setId(rs.getInt("id"));
                    service.setServiceName(rs.getString("name"));
                    service.setDescription(rs.getString("description"));
                    service.setPrice(rs.getBigDecimal("price"));
                    services.add(service);
                }
            }
            reservation.setServices(services);
        } catch (SQLException e) {
            throw new RepositoryException("Error al cargar los servicios de la reserva", e);
        }
    }

    @Override
    public Set<Reservation> getAll() {
        Set<Reservation> reservations = new HashSet<>();
        String sql = "SELECT r.*, s.id as student_id, s.name as student_name, s.surname as student_surname, " +
                    "s.email as student_email, s.phone as student_phone, " +
                    "rm.id_room as room_id, rm.room_number, rm.floor, rm.capacity, rm.room_type, rm.price as room_price " +
                    "FROM RESERVATION r " +
                    "JOIN STUDENT s ON r.student_id = s.id " +
                    "JOIN ROOM rm ON r.room_id = rm.id_room";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Reservation reservation = mapResultSetToReservation(rs);
                loadReservationServices(reservation);
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
        String sql = "SELECT r.*, s.id as student_id, s.name as student_name, s.surname as student_surname, " +
                    "s.email as student_email, s.phone as student_phone, " +
                    "rm.id_room as room_id, rm.room_number, rm.floor, rm.capacity, rm.room_type, rm.price as room_price " +
                    "FROM RESERVATION r " +
                    "JOIN STUDENT s ON r.student_id = s.id " +
                    "JOIN ROOM rm ON r.room_id = rm.id_room " +
                    "WHERE r.start_date <= ? AND r.end_date >= ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, java.sql.Date.valueOf(date));
            stmt.setDate(2, java.sql.Date.valueOf(date));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Reservation reservation = mapResultSetToReservation(rs);
                    loadReservationServices(reservation);
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
        String sql = "SELECT r.*, s.id as student_id, s.name as student_name, s.surname as student_surname, " +
                    "s.email as student_email, s.phone as student_phone, " +
                    "rm.id_room as room_id, rm.room_number, rm.floor, rm.capacity, rm.room_type, rm.price as room_price " +
                    "FROM RESERVATION r " +
                    "JOIN STUDENT s ON r.student_id = s.id " +
                    "JOIN ROOM rm ON r.room_id = rm.id_room " +
                    "WHERE r.student_id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Reservation reservation = mapResultSetToReservation(rs);
                    loadReservationServices(reservation);
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

        AddressImpl address = new AddressImpl();
        address.setId(rs.getInt("id"));
        address.setStreet(rs.getString("street"));
        address.setCity(rs.getString("city"));
        address.setZipCode(rs.getString("zip_code"));
        address.setCountry(rs.getString("country"));
        student.setAddress(Set.of(address));
        Set<Student> students = new HashSet<>();
        students.add(student);
        reservation.setStudent(students);

        RoomImpl room = new RoomImpl();
        room.setId(rs.getInt("room_id"));
        room.setRoomNumber(rs.getString("room_number"));
        room.setFloor(rs.getInt("floor"));
        room.setCapacity(rs.getInt("capacity"));
        room.setType(rs.getString("room_type"));
        room.setPrice(rs.getBigDecimal("room_price"));
        Set<Room> rooms = new HashSet<>();
        rooms.add(room);
        reservation.setRoom(rooms);

        return reservation;
    }
}