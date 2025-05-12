package cat.uvic.teknos.dam.ruvic.jdbc.classes;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Payment {
    private int id_payments;
    private Reservation reservation_id;
    private BigDecimal amount;
    private LocalDate payment_date;
    private String payment_method;
    private String status;

    public int getId() {
        return id_payments;
    }

    public void setId(int id) {
        this.id_payments = id;
    }

    public Reservation getReservation() {
        return reservation_id;
    }

    public void setReservation(Reservation reservation) {
        this.reservation_id = reservation;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getPaymentDate() {
        return payment_date;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.payment_date = paymentDate;
    }

    public String getPaymentMethod() {
        return payment_method;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.payment_method = paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}