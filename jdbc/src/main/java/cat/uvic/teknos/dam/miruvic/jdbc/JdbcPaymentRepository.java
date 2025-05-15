package cat.uvic.teknos.dam.miruvic.jdbc;

import java.sql.*;
import java.util.*;
import cat.uvic.teknos.dam.miruvic.Payment;
import cat.uvic.teknos.dam.miruvic.PaymentRepository;
import cat.uvic.teknos.dam.miruvic.impl.PaymentImpl;

public class JdbcPaymentRepository implements PaymentRepository<Payment> {

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/RUVIC", "root", "rootpassword");
    }

    @Override
    public void save(Payment value) {
        String sql;
        if (value.getId() == 0) {
            sql = "INSERT INTO PAYMENT (amount, payment_date, payment_method) VALUES (?, ?, ?)";
        } else {
            sql = "UPDATE PAYMENT SET amount = ?, payment_date = ?, payment_method = ? WHERE id_payments = ?";
        }

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setBigDecimal(1, value.getAmount());
            stmt.setDate(2, new java.sql.Date(value.getPaymentDate().getTime()));
            stmt.setString(3, value.getPaymentMethod());

            if (value.getId() != 0) {
                stmt.setInt(4, value.getId());
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
            throw new RuntimeException("Error saving payment", e);
        }
    }

    @Override
    public void delete(Payment value) {
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement("DELETE FROM PAYMENT WHERE id_payments = ?")) {
            stmt.setInt(1, value.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting payment", e);
        }
    }

    @Override
    public Payment get(Integer id) {
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM PAYMENT WHERE id_payments = ?")) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    var payment = new PaymentImpl();
                    payment.setId(rs.getInt("id_payments"));
                    payment.setAmount(rs.getBigDecimal("amount"));
                    payment.setPaymentDate(rs.getDate("payment_date"));
                    payment.setPaymentMethod(rs.getString("payment_method"));
                    return payment;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting payment", e);
        }
        return null;
    }

    @Override
    public Set<Payment> getAll() {
        Set<Payment> payments = new HashSet<>();

        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM PAYMENT")) {

            while (rs.next()) {
                var payment = new PaymentImpl();
                payment.setId(rs.getInt("id_payments"));
                payment.setAmount(rs.getBigDecimal("amount"));
                payment.setPaymentDate(rs.getDate("payment_date"));
                payment.setPaymentMethod(rs.getString("payment_method"));
                payments.add(payment);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting all payments", e);
        }

        return payments;
    }
}