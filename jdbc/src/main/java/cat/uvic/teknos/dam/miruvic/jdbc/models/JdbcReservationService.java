package cat.uvic.teknos.dam.miruvic.jdbc.models;

import cat.uvic.teknos.dam.miruvic.Reservation;
import cat.uvic.teknos.dam.miruvic.Service;
import cat.uvic.teknos.dam.miruvic.ReservationService;

public class JdbcReservationService implements ReservationService {
    private Reservation reservation;
    private Service service;

    @Override
    public Reservation getReservation() {
        return reservation;
    }

    @Override
    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    @Override
    public Service getService() {
        return service;
    }

    @Override
    public void setService(Service service) {
        this.service = service;
    }
}