package cat.uvic.teknos.dam.miruvic.jdbc.repositories;

import java.sql.*;
import java.util.*;
import cat.uvic.teknos.dam.miruvic.model.Payment;
import cat.uvic.teknos.dam.miruvic.model.Reservation;
import cat.uvic.teknos.dam.miruvic.model.impl.ReservationImpl;
import cat.uvic.teknos.dam.miruvic.repositories.PaymentRepository;
import cat.uvic.teknos.dam.miruvic.model.impl.PaymentImpl;
import cat.uvic.teknos.dam.miruvic.jdbc.exceptions.*;
import cat.uvic.teknos.dam.miruvic.jdbc.datasources.DataSource;

public class JdbcPaymentRepository implements PaymentRepository {

    private final DataSource dataSource;

    public JdbcPaymentRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void save(Payment payment) {
        String sql;
        boolean isInsert = payment.getId() == null || payment.getId() == 0;
        if (isInsert) {
            sql = "INSERT INTO PAYMENT (reservation_id, amount, payment_date, payment_method, status) VALUES (?, ?, ?, ?, ?)";
        } else {
            sql = "UPDATE PAYMENT SET reservation_id = ?, amount = ?, payment_date = ?, payment_method = ?, status = ? WHERE id = ?";
        }

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            Reservation reservation = payment.getReservation().stream().findFirst().orElse(null);
            if (reservation == null) {
                throw new RepositoryException("El pago debe estar asociado a una reserva");
            }

            stmt.setInt(1, reservation.getId());
            stmt.setBigDecimal(2, payment.getAmount());
            stmt.setDate(3, java.sql.Date.valueOf(payment.getPaymentDate()));
            stmt.setString(4, payment.getPaymentMethod());
            stmt.setString(5, payment.getStatus());

            if (!isInsert) {
                stmt.setInt(6, payment.getId());
            }

            stmt.executeUpdate();

            if (isInsert) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        payment.setId(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataSourceException("Error al guardar el pago", e);
        }
    }

    @Override
    public void delete(Payment value) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM PAYMENT WHERE id = ?")) {
            stmt.setInt(1, value.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Error deleting payment", e);
        }
    }

    @Override
    public Payment get(Integer id) {
        Payment payment = new PaymentImpl();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM PAYMENT WHERE id = ?")) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    payment = mapResultSetToPayment(rs);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error getting payment", e);
        }
        return payment;
    }

    @Override
    public Set<Payment> getAll() {
        Set<Payment> payments = new HashSet<>();
        try (Connection conn = dataSource.getConnection();
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
    public List<Payment> findByAmountRange(double minAmount, double maxAmount) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM PAYMENT WHERE amount BETWEEN ? AND ?";

        try (Connection conn = dataSource.getConnection();
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
        return payments;
    }

    @Override
    public List<Payment> findByMethod(String method) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM PAYMENT WHERE payment_method = ?";

        try (Connection conn = dataSource.getConnection();
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
        return payments;
    }

    private Payment mapResultSetToPayment(ResultSet rs) {
        Payment payment = new PaymentImpl();
        try {
            payment.setId(rs.getInt("id"));

            Reservation reservation = new ReservationImpl();
            reservation.setId(rs.getInt("id_reservation"));

            payment.setReservation(Set.of(reservation));
            payment.setAmount(rs.getBigDecimal("amount"));
            payment.setPaymentDate(rs.getDate("payment_date").toLocalDate());
            payment.setPaymentMethod(rs.getString("payment_method"));
            payment.setStatus(rs.getString("status"));
        } catch (SQLException e) {
            throw new RepositoryException("Error mapping payment", e);
        }
        return payment;
    }
}