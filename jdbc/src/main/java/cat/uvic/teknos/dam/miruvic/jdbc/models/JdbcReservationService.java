package cat.uvic.teknos.dam.miruvic.jdbc.models;

import cat.uvic.teknos.dam.miruvic.model.Reservation;
import cat.uvic.teknos.dam.miruvic.model.Service;
import cat.uvic.teknos.dam.miruvic.model.ReservationService;

public class JdbcReservationService implements ReservationService {
    private Reservation reservation;
    private Service services;
    private Integer quantity;

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
        return services;
    }

    @Override
    public void setService(Service services) {
        this.services = services;
    }

    @Override
    public Integer getQuantity() {
        return quantity;
    }

    @Override
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}