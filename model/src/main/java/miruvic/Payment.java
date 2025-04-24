package miruvic;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface Payment {
    // Métodos de la interfaz
    int getId();

    void setId(int id);

    Reservation getReservation();

    void setReservation(Reservation reservation);

    BigDecimal getAmount();

    void setAmount(BigDecimal amount);

    LocalDate getPaymentDate();

    void setPaymentDate(LocalDate paymentDate);

    PaymentMethod getPaymentMethod();

    void setPaymentMethod(PaymentMethod paymentMethod);

    PaymentStatus getStatus();

    void setStatus(PaymentStatus status);

    // Enums para los métodos de pago y estados
    enum PaymentMethod {
        Card, Transfer
    }

    enum PaymentStatus {
        Completed, Pending
    }
}