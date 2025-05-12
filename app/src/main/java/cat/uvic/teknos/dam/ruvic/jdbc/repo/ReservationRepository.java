package cat.uvic.teknos.dam.ruvic.jdbc.repo;

import java.util.List;
import java.sql.*;
import java.util.ArrayList;
import cat.uvic.teknos.dam.ruvic.jdbc.classes.Reservation;
import cat.uvic.teknos.dam.ruvic.jdbc.classes.Room;
import cat.uvic.teknos.dam.ruvic.jdbc.classes.Student;

public class ReservationRepository {
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/RUVIC", "root", "rootpassword");
    }

    public void save(Reservation reservation) {
        String sql;
        if (reservation.getId() == 0) {
            sql = "INSERT INTO RESERVATION (start_date, end_date, room_id, student_id) VALUES (?, ?, ?, ?)";
        } else {
            sql = "UPDATE RESERVATION SET start_date = ?, end_date = ?, room_id = ?, student_id = ? WHERE id = ?";
        }
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setDate(1, Date.valueOf(reservation.getStartDate()));
            stmt.setDate(2, Date.valueOf(reservation.getEndDate()));
            stmt.setInt(3, reservation.getRoom().getId());
            stmt.setInt(4, reservation.getStudent().getId());
            if (reservation.getId() != 0) {
                stmt.setInt(5, reservation.getId());
            }
            stmt.executeUpdate();
            if (reservation.getId() == 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        reservation.setId(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(Reservation reservation) {
        String sql = "DELETE FROM RESERVATION WHERE id = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, reservation.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Reservation get(int id) {
        String sql = "SELECT * FROM RESERVATION WHERE id = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Reservation reservation = new Reservation();
                    reservation.setId(rs.getInt("id"));
                    reservation.setStartDate(rs.getDate("start_date").toLocalDate());
                    reservation.setEndDate(rs.getDate("end_date").toLocalDate());
                    reservation.setRoom(new Room());
                    reservation.setStudent(new Student());
                    return reservation;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Reservation> getAll() {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM RESERVATION";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Reservation reservation = new Reservation();
                reservation.setId(rs.getInt("id"));
                reservation.setStartDate(rs.getDate("start_date").toLocalDate());
                reservation.setEndDate(rs.getDate("end_date").toLocalDate());
                reservation.setRoom(new Room());
                reservation.setStudent(new Student());
                reservations.add(reservation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservations;
    }
}