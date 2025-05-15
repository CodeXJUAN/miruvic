package cat.uvic.teknos.dam.miruvic.jdbc;

import java.sql.*;
import java.util.*;
import cat.uvic.teknos.dam.miruvic.Reservation;
import cat.uvic.teknos.dam.miruvic.impl.ReservationImpl;
import cat.uvic.teknos.dam.miruvic.ReservationRepository;

public class JdbcReservationRepository implements ReservationRepository {

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/RUVIC", "root", "rootpassword");
    }

    @Override
    public void save(Reservation value) {
        String sql;
        if (value.getId() == 0) {
            sql = "INSERT INTO RESERVATION (room_id, guest_id, check_in_date, check_out_date, total_price) VALUES (?, ?, ?, ?, ?)";
        } else {
            sql = "UPDATE RESERVATION SET room_id = ?, guest_id = ?, check_in_date = ?, check_out_date = ?, total_price = ? WHERE id_reservation = ?";
        }

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, value.getRoomId());
            stmt.setInt(2, value.getGuestId());
            stmt.setDate(3, Date.valueOf(value.getCheckInDate()));
            stmt.setDate(4, Date.valueOf(value.getCheckOutDate()));
            stmt.setDouble(5, value.getTotalPrice());

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
            throw new RuntimeException("Error saving reservation", e);
        }
    }

    @Override
    public void delete(Reservation value) {
        String sql = "DELETE FROM RESERVATION WHERE id_reservation = ?";

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, value.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting reservation", e);
        }
    }

    @Override
    public Reservation get(Integer id) {
        String sql = "SELECT * FROM RESERVATION WHERE id_reservation = ?";

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Reservation reservation = new ReservationImpl();
                    reservation.setId(rs.getInt("id_reservation"));
                    reservation.setRoomId(rs.getInt("room_id"));
                    reservation.setGuestId(rs.getInt("guest_id"));
                    reservation.setCheckInDate(rs.getDate("check_in_date").toLocalDate());
                    reservation.setCheckOutDate(rs.getDate("check_out_date").toLocalDate());
                    reservation.setTotalPrice(rs.getDouble("total_price"));
                    return reservation;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting reservation", e);
        }
        return null;
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
                reservation.setId(rs.getInt("id_reservation"));
                reservation.setRoomId(rs.getInt("room_id"));
                reservation.setGuestId(rs.getInt("guest_id"));
                reservation.setCheckInDate(rs.getDate("check_in_date").toLocalDate());
                reservation.setCheckOutDate(rs.getDate("check_out_date").toLocalDate());
                reservation.setTotalPrice(rs.getDouble("total_price"));
                reservations.add(reservation);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting all reservations", e);
        }
        return reservations;
    }
}
