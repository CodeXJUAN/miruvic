package cat.uvic.teknos.dam.miruvic.jdbc.repositories;

import java.sql.*;
import java.util.*;
import java.io.*;
import cat.uvic.teknos.dam.miruvic.model.Payment;
import cat.uvic.teknos.dam.miruvic.repositories.PaymentRepository;
import cat.uvic.teknos.dam.miruvic.model.impl.PaymentImpl;
import cat.uvic.teknos.dam.miruvic.jdbc.exceptions.*;
import cat.uvic.teknos.dam.miruvic.jdbc.datasources.DataSource;

public class JdbcPaymentRepository implements PaymentRepository<Payment> {
    
    private final DataSource dataSource;

    public JdbcPaymentRepository(DataSource dataSource) {
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
            throw new DataSourceException("Error saving payment", e);
        }
    }

    @Override
    public void delete(Payment value) {
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement("DELETE FROM PAYMENT WHERE id_payments = ?")) {
            stmt.setInt(1, value.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Error deleting payment", e);
        }
    }

    @Override
    public Payment get(Integer id) {
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM PAYMENT WHERE id_payments = ?")) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPayment(rs);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error getting payment", e);
        }
        throw new EntityNotFoundException("Payment not found with id: " + id);
    }

    @Override
    public Set<Payment> getAll() {
        Set<Payment> payments = new HashSet<>();

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM PAYMENT");
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                payments.add(mapResultSetToPayment(rs));
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error getting all payments", e);
        }

        return payments;
    }

    @Override
    public Payment findByAmountRange(double minAmount, double maxAmount) {
        Set<Payment> payments = new HashSet<>();
        String sql = "SELECT * FROM PAYMENT WHERE amount BETWEEN ? AND ?";

        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDouble(1, minAmount);
            stmt.setDouble(2, maxAmount);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    payments.add(mapResultSetToPayment(rs));
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error finding payments by amount range", e);
        }

        return (Payment) payments;
    }

    @Override
    public Payment findByMethod(String method) {
        Set<Payment> payments = new HashSet<>();
        String sql = "SELECT * FROM PAYMENT WHERE payment_method = ?";

        try (Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, method);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    payments.add(mapResultSetToPayment(rs));
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error finding payments by method", e);
        }

        return (Payment) payments;
    }

    private Payment mapResultSetToPayment(ResultSet rs) {
        try {
            Payment payments = new PaymentImpl();
            payments.setId(rs.getInt("id_payments"));
            payments.setAmount(rs.getBigDecimal("amount"));
            payments.setPaymentDate(rs.getDate("payment_date").toLocalDate());
            payments.setPaymentMethod(Payment.PaymentMethod.valueOf(rs.getString("payment_method")));
            return payments;
        } catch (SQLException e) {
            throw new RepositoryException("Error al mapear los datos de la habitación", e);
        }
    }
}