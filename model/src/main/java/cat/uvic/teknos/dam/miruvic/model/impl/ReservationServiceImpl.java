package cat.uvic.teknos.dam.miruvic.model.impl;

import cat.uvic.teknos.dam.miruvic.Reservation;
import cat.uvic.teknos.dam.miruvic.Service;

public class ReservationServiceImpl implements cat.uvic.teknos.dam.miruvic.ReservationService {
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