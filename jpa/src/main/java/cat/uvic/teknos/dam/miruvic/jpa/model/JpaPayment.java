package cat.uvic.teknos.dam.miruvic.model.jpa;

import cat.uvic.teknos.dam.miruvic.model.Payment;
import cat.uvic.teknos.dam.miruvic.model.Reservation;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "PAYMENT")
@NoArgsConstructor
@Getter
@Setter
public class JpaPayment implements Payment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reservation_id")
    private JpaReservation reservation;
    
    @Column(name = "amount")
    private BigDecimal amount;
    
    @Column(name = "payment_date")
    private LocalDate paymentDate;
    
    @Column(name = "payment_method")
    private String paymentMethod;
    
    @Column(name = "status")
    private String status;
    
    @Override
    public Reservation getReservation() {
        return reservation;
    }
    
    @Override
    public void setReservation(Reservation reservation) {
        if (reservation instanceof JpaReservation) {
            this.reservation = (JpaReservation) reservation;
        } else {
            throw new IllegalArgumentException("Reservation must be a JpaReservation instance");
        }
    }
}
