package cat.uvic.teknos.dam.miruvic.model;

import java.util.Set;

public interface ReservationService {
    Set<Reservation> getReservation();
    Set<Service> getService();

    void setReservation(Set<Reservation> reservation);
    void setService(Set<Service> services);
}