package cat.uvic.teknos.dam.miruvic.jpa.model;

import cat.uvic.teknos.dam.miruvic.model.ReservationService;
import cat.uvic.teknos.dam.miruvic.model.Reservation;
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

    @EmbeddedId
    private ReservationServiceId id = new ReservationServiceId();

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("reservationId")
    @JoinColumn(name = "reservation_id")
    private JpaReservation reservation;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("serviceId")
    @JoinColumn(name = "service_id")
    private JpaService service;

    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;

    @Override
    public Reservation getReservation() {
        return reservation;
    }

    @Override
    public void setReservation(Reservation reservation) {
        if (reservation instanceof JpaReservation) {
            this.reservation = (JpaReservation) reservation;
            this.id.setReservationId(this.reservation.getId());
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
            this.id.setServiceId(this.service.getId());
        }
    }
}
