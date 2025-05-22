package cat.uvic.teknos.dam.miruvic.model;

import java.util.Set;

public interface ReservationService {
    public Set<Reservation> getReservation();

    public void setReservation(Reservation reservation);

    public Set<Service> getService();

    public void setService(Service service);
}