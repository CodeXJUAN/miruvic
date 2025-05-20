package cat.uvic.teknos.dam.miruvic.jdbc.repositories;

import java.sql.*;
import java.util.*;
import java.io.*;
import cat.uvic.teknos.dam.miruvic.Payment;
import cat.uvic.teknos.dam.miruvic.PaymentRepository;
import cat.uvic.teknos.dam.miruvic.impl.PaymentImpl;

public class JdbcPaymentRepository implements PaymentRepository<Payment> {
    
    private Connection getConnection() throws SQLException {
        var properties = new Properties();
        try {
            properties.load(new FileInputStream("datasoruce.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String driver = properties.getProperty("driver");
        String server = properties.getProperty("server");
        String database = properties.getProperty("database");
        String username = properties.getProperty("username");
        String password = properties.getProperty("password");
        return DriverManager.getConnection(String.format("jdbc:%s://%s/%s", driver, server, database),
                username, password);
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
            stmt.setDate(2, java.sql.Date.valueOf(value.getPaymentDate()));
            stmt.setString(3, value.getPaymentMethod().toString());

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
                    payment.setPaymentDate(rs.getDate("payment_date").toLocalDate());
                    payment.setPaymentMethod(Payment.PaymentMethod.valueOf(rs.getString("payment_method")));
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
                payment.setPaymentDate(rs.getDate("payment_date").toLocalDate());
                payment.setPaymentMethod(Payment.PaymentMethod.valueOf(rs.getString("payment_method")));
                payments.add(payment);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting all payments", e);
        }

        return payments;
    }

    @Override
    public List<Payment> findByAmountRange(double minAmount, double maxAmount) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM PAYMENT WHERE amount BETWEEN ? AND ?";

        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDouble(1, minAmount);
            stmt.setDouble(2, maxAmount);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    var payment = new PaymentImpl();
                    payment.setId(rs.getInt("id_payments"));
                    payment.setAmount(rs.getBigDecimal("amount"));
                    payment.setPaymentDate(rs.getDate("payment_date").toLocalDate());
                    payment.setPaymentMethod(Payment.PaymentMethod.valueOf(rs.getString("payment_method")));
                    payments.add(payment);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding payments by amount range", e);
        }

        return payments;
    }

    @Override
    public List<Payment> findByMethod(String method) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM PAYMENT WHERE payment_method = ?";

        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, method);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    var payment = new PaymentImpl();
                    payment.setId(rs.getInt("id_payments"));
                    payment.setAmount(rs.getBigDecimal("amount"));
                    payment.setPaymentDate(rs.getDate("payment_date").toLocalDate());
                    payment.setPaymentMethod(Payment.PaymentMethod.valueOf(rs.getString("payment_method")));
                    payments.add(payment);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding payments by method", e);
        }

        return payments;
    }
}