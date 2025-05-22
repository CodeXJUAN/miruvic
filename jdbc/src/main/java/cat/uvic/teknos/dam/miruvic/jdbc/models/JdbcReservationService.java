package cat.uvic.teknos.dam.miruvic.jdbc.models;

import java.util.Set;
import cat.uvic.teknos.dam.miruvic.model.Reservation;
import cat.uvic.teknos.dam.miruvic.model.Service;
import cat.uvic.teknos.dam.miruvic.model.ReservationService;

public class JdbcReservationService implements ReservationService {
    private Set<Reservation> reservation;
    private Set<Service> service;

    @Override
    public Set<Reservation> getReservation() {
        return reservation != null ? reservation : Set.of();
    }

    @Override
    public void setReservation(Reservation reservation) {
        this.reservation = reservation != null ? Set.of(reservation) : Set.of();
    }

    @Override
    public Set<Service> getService() {
        return service != null ? service : Set.of();
    }

    @Override
    public void setService(Service service) {
        this.service = service != null ? Set.of(service) : Set.of();
    }
}