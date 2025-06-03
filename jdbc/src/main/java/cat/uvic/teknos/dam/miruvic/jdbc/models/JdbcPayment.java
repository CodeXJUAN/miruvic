package cat.uvic.teknos.dam.miruvic.jdbc.models;

import cat.uvic.teknos.dam.miruvic.model.Reservation;
import cat.uvic.teknos.dam.miruvic.model.Payment;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public class JdbcPayment implements Payment {
    private Integer id;
    private Reservation reservation;
    private BigDecimal amount;
    private LocalDate paymentDate;
    private String paymentMethod;
    private String status;
    private Set<Reservation> reservations;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public Set<Reservation> getReservation() {
        return Set.of();
    }

    @Override
    public void setReservation(Set<Reservation> reservation) {
        this.reservations = reservation;
    }

    @Override
    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    @Override
    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    @Override
    public String getPaymentMethod() {
        return paymentMethod;
    }

    @Override
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }
}