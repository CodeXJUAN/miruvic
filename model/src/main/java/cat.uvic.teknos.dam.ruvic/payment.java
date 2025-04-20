package cat.uvic.teknos.dam.ruvic;

import java.math.BigDecimal;
import java.time.LocalDate; // Cambiado de Date a LocalDate para date SQL

public class interface Payment {
    private int id;
    private Reservation reservation;
    private BigDecimal amount; // Cambiado de double a BigDecimal para decimal(6,2)
    private LocalDate paymentDate; // Cambiado de Date a LocalDate
    private PaymentMethod paymentMethod; // Enum para Card, Transfer
    private PaymentStatus status; // Enum para Completed, Pending

    // Enums para los métodos de pago y estados
    public enum PaymentMethod {
        Card, Transfer
    }

    public enum PaymentStatus {
        Completed, Pending
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }
}