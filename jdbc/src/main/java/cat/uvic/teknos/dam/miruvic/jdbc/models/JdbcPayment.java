package cat.uvic.teknos.dam.miruvic.jdbc.models;

import cat.uvic.teknos.dam.miruvic.model.Reservation;
import cat.uvic.teknos.dam.miruvic.model.Payment;
import java.math.BigDecimal;
import java.time.LocalDate;

public class JdbcPayment implements Payment {
    private int id;
    private Reservation reservation;
    private BigDecimal amount;
    private LocalDate paymentDate;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public Reservation getReservation() {
        return reservation;
    }

    @Override
    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
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
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    @Override
    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    @Override
    public PaymentStatus getStatus() {
        return status;
    }

    @Override
    public void setStatus(PaymentStatus status) {
        this.status = status;
    }
}