package cat.uvic.teknos.dam.miruvic.jpa.model;

import cat.uvic.teknos.dam.miruvic.model.Reservation;
import cat.uvic.teknos.dam.miruvic.model.ReservationService;
import cat.uvic.teknos.dam.miruvic.model.Service;
import jakarta.persistence.*;
import java.util.Collections;
import java.util.Set;
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
    private JpaReservationServiceId id;

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
    public Set<Reservation> getReservation() {
        return reservation == null ? Collections.emptySet() : Set.of(reservation);
    }

    @Override
    public void setReservation(Set<Reservation> reservations) {
        if (reservations != null && !reservations.isEmpty()) {
            Reservation res = reservations.iterator().next();
            if (res instanceof JpaReservation) {
                this.reservation = (JpaReservation) res;
            } else {
                throw new IllegalArgumentException("Reservation must be a JpaReservation instance");
            }
        }
    }

    @Override
    public Set<Service> getService() {
        return service == null ? java.util.Collections.emptySet() : java.util.Set.of(service);
    }

    @Override
    public void setService(Set<Service> services) {
        if (services != null && !services.isEmpty()) {
            Service s = services.iterator().next();
            if (s instanceof JpaService) {
                this.service = (JpaService) s;
            } else {
                throw new IllegalArgumentException("Service must be a JpaService instance");
            }
        }
    }
}
