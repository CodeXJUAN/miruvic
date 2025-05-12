package cat.uvic.teknos.dam.ruvic.jdbc.repo;

import java.util.List;
import java.sql.*;
import java.util.ArrayList;
import cat.uvic.teknos.dam.ruvic.jdbc.classes.Payment;
import cat.uvic.teknos.dam.ruvic.jdbc.classes.Reservation;

public class PaymentRepository {
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/RUVIC", "root", "rootpassword");
    }

    public void save(Payment payment) {
        String sql;
        if (payment.getId() == 0) {
            sql = "INSERT INTO PAYMENT (reservation_id, amount, payment_date, payment_method, status) VALUES (?, ?, ?, ?, ?)";
        } else {
            sql = "UPDATE PAYMENT SET reservation_id = ?, amount = ?, payment_date = ?, payment_method = ?, status = ? WHERE id_payments = ?";
        }
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, payment.getReservation().getId());
            stmt.setBigDecimal(2, payment.getAmount());
            stmt.setDate(3, Date.valueOf(payment.getPaymentDate()));
            stmt.setString(4, payment.getPaymentMethod());
            stmt.setString(5, payment.getStatus());
            if (payment.getId() != 0) {
                stmt.setInt(6, payment.getId());
            }
            stmt.executeUpdate();
            if (payment.getId() == 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        payment.setId(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(Payment payment) {
        String sql = "DELETE FROM PAYMENT WHERE id_payments = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, payment.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Payment get(int id) {
        String sql = "SELECT * FROM PAYMENT WHERE id_payments = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Payment payment = new Payment();
                    payment.setId(rs.getInt("id_payments"));
                    payment.setReservation(new Reservation());
                    payment.setAmount(rs.getBigDecimal("amount"));
                    payment.setPaymentDate(rs.getDate("payment_date").toLocalDate());
                    payment.setPaymentMethod(rs.getString("payment_method"));
                    payment.setStatus(rs.getString("status"));
                    return payment;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Payment> getAll() {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM PAYMENT";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Payment payment = new Payment();
                payment.setId(rs.getInt("id_payments"));
                payment.setReservation(new Reservation());
                payment.setAmount(rs.getBigDecimal("amount"));
                payment.setPaymentDate(rs.getDate("payment_date").toLocalDate());
                payment.setPaymentMethod(rs.getString("payment_method"));
                payment.setStatus(rs.getString("status"));
                payments.add(payment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return payments;
    }
}