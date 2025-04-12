package cat.uvic.teknos.dam.ruvic;

public class ReservationService {
    private Reservation reservation;
    private Service service;

    // Getters and setters
    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }
}