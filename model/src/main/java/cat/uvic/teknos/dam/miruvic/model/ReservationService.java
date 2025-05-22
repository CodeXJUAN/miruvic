package cat.uvic.teknos.dam.miruvic.model;

public interface ReservationService {
    Reservation getReservation();

    void setReservation(Reservation reservation);

    Service getService();

    void setService(Service service);
}