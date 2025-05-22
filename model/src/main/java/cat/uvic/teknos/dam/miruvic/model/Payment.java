package cat.uvic.teknos.dam.miruvic.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface Payment {
    public int getId();

    public void setId(int id);

    public Reservation getReservation();

    public void setReservation(Reservation reservation);

    public BigDecimal getAmount();

    public void setAmount(BigDecimal amount);

    public LocalDate getPaymentDate();

    public void setPaymentDate(LocalDate paymentDate);

    public PaymentMethod getPaymentMethod();

    public void setPaymentMethod(PaymentMethod paymentMethod);

    public PaymentStatus getStatus();

    public void setStatus(PaymentStatus status);

    // Enums para los métodos de pago y estados
    public enum PaymentMethod {
        Card, Transfer
    }

    public enum PaymentStatus {
        Completed, Pending
    }
}