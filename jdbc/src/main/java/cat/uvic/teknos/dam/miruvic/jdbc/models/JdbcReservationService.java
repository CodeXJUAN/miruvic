package cat.uvic.teknos.dam.miruvic.jdbc.models;

import java.util.Set;
import cat.uvic.teknos.dam.miruvic.model.Reservation;
import cat.uvic.teknos.dam.miruvic.model.Service;
import cat.uvic.teknos.dam.miruvic.model.ReservationService;

public class JdbcReservationService implements ReservationService {
    private Set<Reservation> reservation;
    private Set<Service> services;

    @Override
    public Set<Reservation> getReservation() {
        return Set.of();
    }

    @Override
    public void setReservation(Set<Reservation> reservation) {
        this.reservation = reservation;
    }

    @Override
    public Set<Service> getService() {
        return Set.of();
    }

    @Override
    public void setService(Set<Service> services) {
        this.services = services;
    }
}