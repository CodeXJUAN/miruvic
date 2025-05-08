package cat.uvic.teknos.dam.miruvic;

;

public interface ReservationService {
    Reservation getReservation();

    void setReservation(Reservation reservation);

    Service getService();

    void setService(Service service);
}