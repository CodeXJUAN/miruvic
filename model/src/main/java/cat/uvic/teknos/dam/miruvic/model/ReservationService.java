package cat.uvic.teknos.dam.miruvic.model;

public interface ReservationService {
    Reservation getReservation();
    Service getService();
    Integer getQuantity();

    void setReservation(Reservation reservation);
    void setService(Service service);
    void setQuantity(Integer quantity);
}
