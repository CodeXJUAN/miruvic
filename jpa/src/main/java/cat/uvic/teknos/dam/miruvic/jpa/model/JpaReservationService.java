package cat.uvic.teknos.dam.miruvic.model.jpa;

import cat.uvic.teknos.dam.miruvic.model.Reservation;
import cat.uvic.teknos.dam.miruvic.model.ReservationService;
import cat.uvic.teknos.dam.miruvic.model.Service;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "RESERVATION_SERVICE")
@NoArgsConstructor
@Getter
@Setter
public class JpaReservationService implements ReservationService {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reservation_id")
    private JpaReservation reservation;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "service_id")
    private JpaService service;
    
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
    
    @Override
    public Service getService() {
        return service;
    }
    
    @Override
    public void setService(Service service) {
        if (service instanceof JpaService) {
            this.service = (JpaService) service;
        } else {
            throw new IllegalArgumentException("Service must be a JpaService instance");
        }
    }
}
