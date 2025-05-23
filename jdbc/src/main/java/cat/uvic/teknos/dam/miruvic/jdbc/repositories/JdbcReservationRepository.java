package cat.uvic.teknos.dam.miruvic.jdbc.repositories;

import java.sql.*;
import java.util.*;
import java.io.*;
import java.time.*;
import cat.uvic.teknos.dam.miruvic.model.Reservation;
import cat.uvic.teknos.dam.miruvic.model.ReservationStatus;
import cat.uvic.teknos.dam.miruvic.model.Student;
import cat.uvic.teknos.dam.miruvic.model.Room;
import cat.uvic.teknos.dam.miruvic.model.impl.ReservationImpl;
import cat.uvic.teknos.dam.miruvic.model.impl.StudentImpl;
import cat.uvic.teknos.dam.miruvic.model.impl.RoomImpl;
import cat.uvic.teknos.dam.miruvic.repositories.ReservationRepository;
import cat.uvic.teknos.dam.miruvic.jdbc.exceptions.*;
import cat.uvic.teknos.dam.miruvic.jdbc.datasources.DataSource;

public class JdbcReservationRepository implements ReservationRepository<Reservation> {

    private final DataSource dataSource;

    public JdbcReservationRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private Connection getConnection() throws DataSourceException {
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
        try {
                return DriverManager.getConnection(String.format("jdbc:%s://%s/%s", driver, server, database),
                        username, password);
            } catch (SQLException e) {
                throw new DataSourceException("Error connecting to database", e);
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
            stmt.setInt(1, ((Reservation) value.getStudent()).getId());
            stmt.setInt(2, ((Reservation) value.getRoom()).getId());
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
            throw new DataSourceException("Error al guardar la reserva", e);
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
            throw new RepositoryException("Error al eliminar la reserva", e);
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
        String sql = "SELECT * FROM RESERVATION";

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                reservations.add(mapResultSetToReservation(rs));
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error al obtener todas las reservas", e);
        }
        return reservations;
    }

    @Override
    public Reservation findByDate(LocalDate date) {
        Set<Reservation> reservations = new HashSet<>();
        String sql = "SELECT * FROM RESERVATION WHERE start_date <= ? AND end_date >= ?";

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, java.sql.Date.valueOf(date));
            stmt.setDate(2, java.sql.Date.valueOf(date));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reservations.add(mapResultSetToReservation(rs));
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error al buscar reservas por fecha", e);
        }
        return (Reservation) reservations;
    }

    @Override
    public Reservation findByStudentId(int studentId) {
        Set<Reservation> reservations = new HashSet<>();
        String sql = "SELECT * FROM RESERVATION WHERE student_id = ?";
        
        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reservations.add(mapResultSetToReservation(rs));
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error al buscar reservas por ID de estudiante", e);
        }
        
        return (Reservation) reservations;
    }
    
    private Reservation mapResultSetToReservation(ResultSet rs) {
        try {
            Reservation reservation = new ReservationImpl();
            reservation.setId(rs.getInt("id"));
            
            Student student = new StudentImpl();
            student.setId(rs.getInt("student_id"));
            reservation.setStudent(student);
            
            Room room = new RoomImpl();
            room.setId(rs.getInt("room_id"));
            reservation.setRoom(room);
            
            reservation.setStartDate(rs.getDate("start_date").toLocalDate());
            reservation.setEndDate(rs.getDate("end_date").toLocalDate());
            reservation.setStatus(ReservationStatus.valueOf(rs.getString("status")));
            
            return reservation;
        } catch (SQLException e) {
            throw new RepositoryException("Error al mapear los datos de la reserva", e);
        }
    }
}