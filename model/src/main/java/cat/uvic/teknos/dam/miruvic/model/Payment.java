package cat.uvic.teknos.dam.miruvic.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface Payment {
    Integer getId();
    Reservation getReservation();
    BigDecimal getAmount();
    LocalDate getPaymentDate();
    String getPaymentMethod();
    String getStatus();

    void setId(Integer id);
    void setReservation(Reservation reservation);
    void setAmount(BigDecimal amount);
    void setPaymentDate(LocalDate paymentDate);
    void setPaymentMethod(String paymentMethod);
    void setStatus(String status);
}
