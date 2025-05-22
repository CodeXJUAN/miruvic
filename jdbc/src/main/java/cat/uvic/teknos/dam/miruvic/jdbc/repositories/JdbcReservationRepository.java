package cat.uvic.teknos.dam.miruvic.jdbc.repositories;

import java.sql.*;
import java.util.*;
import java.io.*;
import java.time.*;
import cat.uvic.teknos.dam.miruvic.model.Reservation;
import cat.uvic.teknos.dam.miruvic.model.Student;
import cat.uvic.teknos.dam.miruvic.model.Room;
import cat.uvic.teknos.dam.miruvic.model.Service;
import cat.uvic.teknos.dam.miruvic.model.impl.ReservationImpl;
import cat.uvic.teknos.dam.miruvic.model.impl.StudentImpl;
import cat.uvic.teknos.dam.miruvic.model.impl.RoomImpl;
import cat.uvic.teknos.dam.miruvic.repositories.ReservationRepository;

public class JdbcReservationRepository implements ReservationRepository<Reservation> {

    private Connection getConnection() throws cat.uvic.teknos.dam.miruvic.jdbc.exceptions.DataSourceException {
        var properties = new Properties();
        try (FileInputStream fis = new FileInputStream("datasource.properties")) {
            properties.load(fis);
        } catch (IOException e) {
            throw new cat.uvic.teknos.dam.miruvic.jdbc.exceptions.DataSourceException("Error al cargar el archivo de propiedades", e);
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
            throw new cat.uvic.teknos.dam.miruvic.jdbc.exceptions.DataSourceException("Error al conectar con la base de datos", e);
        }
    }

    @Override
    public void save(Reservation value) {
        String sql;
        if (value.getId() == 0) {
            sql = "INSERT INTO RESERVATION (student_id, room_id, start_date, end_date, status) VALUES (?, ?, ?, ?, ?)";
        } else {
            sql = "UPDATE RESERVATION SET student_id = ?, room_id = ?, start_date = ?, end_date = ?, status = ? WHERE id = ?";
        }

        try (Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, value.getStudent().getId());
            stmt.setInt(2, value.getRoom().getId());
            stmt.setDate(3, java.sql.Date.valueOf(value.getStartDate()));
            stmt.setDate(4, java.sql.Date.valueOf(value.getEndDate()));
            stmt.setString(5, value.getStatus().toString());

            if (value.getId() != 0) {
                stmt.setInt(6, value.getId());
            }

            stmt.executeUpdate();

            if (value.getId() == 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        value.setId(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw cat.uvic.teknos.dam.miruvic.jdbc.exceptions.ExceptionUtils.convertSQLException("Error al guardar la reserva", e);
        }
    }

    @Override
    public void delete(Reservation value) {
        String sql = "DELETE FROM RESERVATION WHERE id = ?";

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, value.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw cat.uvic.teknos.dam.miruvic.jdbc.exceptions.ExceptionUtils.convertSQLException("Error al eliminar la reserva", e);
        }
    }

    @Override
    public Reservation get(Integer id) {
        String sql = "SELECT * FROM RESERVATION WHERE id = ?";

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Reservation reservation = new ReservationImpl();
                    reservation.setId(rs.getInt("id"));
                    
                    // Use implementation classes instead of interfaces
                    Student student = new StudentImpl();
                    student.setId(rs.getInt("student_id"));
                    reservation.setStudent(student);
                    
                    Room room = new RoomImpl();
                    room.setId(rs.getInt("room_id"));
                    reservation.setRoom(room);
                    
                    reservation.setStartDate(rs.getDate("start_date").toLocalDate());
                    reservation.setEndDate(rs.getDate("end_date").toLocalDate());
                    reservation.setStatus(ReservationStatus.valueOf(rs.getString("status")));
                    
                    // Aquí deberías cargar los servicios asociados a la reserva
                    // Esto requeriría una consulta adicional
                    
                    return reservation;
                }
            }
        } catch (SQLException e) {
            throw cat.uvic.teknos.dam.miruvic.jdbc.exceptions.ExceptionUtils.convertSQLException("Error al obtener la reserva", e);
        }
        throw cat.uvic.teknos.dam.miruvic.jdbc.exceptions.ExceptionUtils.createEntityNotFoundException("Reserva", id);
    }

    @Override
    public Set<Reservation> getAll() {
        Set<Reservation> reservations = new HashSet<>();
        String sql = "SELECT * FROM RESERVATION";

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Reservation reservation = new ReservationImpl();
                reservation.setId(rs.getInt("id"));
                
                // Use implementation classes instead of interfaces
                Student student = new StudentImpl();
                student.setId(rs.getInt("student_id"));
                reservation.setStudent(student);
                
                Room room = new RoomImpl();
                room.setId(rs.getInt("room_id"));
                reservation.setRoom(room);
                
                reservation.setStartDate(rs.getDate("start_date").toLocalDate());
                reservation.setEndDate(rs.getDate("end_date").toLocalDate());
                reservation.setStatus(ReservationStatus.valueOf(rs.getString("status")));
                
                // Aquí deberías cargar los servicios asociados a la reserva
                // Esto requeriría una consulta adicional
                
                reservations.add(reservation);
            }
        } catch (SQLException e) {
            throw cat.uvic.teknos.dam.miruvic.jdbc.exceptions.ExceptionUtils.convertSQLException("Error al obtener todas las reservas", e);
        }
        return reservations;
    }

    @Override
    public List<Reservation> findByDate(LocalDate date) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM RESERVATION WHERE start_date <= ? AND end_date >= ?";

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, java.sql.Date.valueOf(date));
            stmt.setDate(2, java.sql.Date.valueOf(date));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Reservation reservation = new ReservationImpl();
                    reservation.setId(rs.getInt("id"));
                    
                    // Use implementation classes instead of interfaces
                    Student student = new StudentImpl();
                    student.setId(rs.getInt("student_id"));
                    reservation.setStudent(student);
                    
                    Room room = new RoomImpl();
                    room.setId(rs.getInt("room_id"));
                    reservation.setRoom(room);
                    
                    reservation.setStartDate(rs.getDate("start_date").toLocalDate());
                    reservation.setEndDate(rs.getDate("end_date").toLocalDate());
                    reservation.setStatus(ReservationStatus.valueOf(rs.getString("status")));
                    
                    // Aquí deberías cargar los servicios asociados a la reserva
                    // Esto requeriría una consulta adicional
                    
                    reservations.add(reservation);
                }
            }
        } catch (SQLException e) {
            throw cat.uvic.teknos.dam.miruvic.jdbc.exceptions.ExceptionUtils.convertSQLException("Error al buscar reservas por fecha", e);
        }
        return reservations;
    }

    @Override
    public List<Reservation> findByStudentId(int studentId) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM RESERVATION WHERE student_id = ?";
        
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Reservation reservation = new ReservationImpl();
                    reservation.setId(rs.getInt("id"));
                    
                    // Aquí deberías cargar el estudiante y la habitación completos
                    // Esto es un ejemplo simplificado
                    Student student = new Student();
                    student.setId(rs.getInt("student_id"));
                    reservation.setStudent(student);
                    
                    Room room = new Room();
                    room.setId(rs.getInt("room_id"));
                    reservation.setRoom(room);
                    
                    reservation.setStartDate(rs.getDate("start_date").toLocalDate());
                    reservation.setEndDate(rs.getDate("end_date").toLocalDate());
                    reservation.setStatus(ReservationStatus.valueOf(rs.getString("status")));
                    
                    // Aquí deberías cargar los servicios asociados a la reserva
                    // Esto requeriría una consulta adicional
                    
                    reservations.add(reservation);
                }
            }
        } catch (SQLException e) {
            throw cat.uvic.teknos.dam.miruvic.jdbc.exceptions.ExceptionUtils.convertSQLException("Error al buscar reservas por ID de estudiante", e);
        }
        
        return reservations;
    }
}