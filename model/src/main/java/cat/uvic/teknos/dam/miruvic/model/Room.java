package cat.uvic.teknos.dam.miruvic.model;

import java.math.BigDecimal;
import java.util.Set;

public interface Room {

    Integer getId();
    String getRoomNumber();
    Integer getFloor();
    Integer getCapacity();
    String getType();
    BigDecimal getPrice();
    Set<Reservation> getReservations();



    void setId(Integer id);
    void setRoomNumber(String roomNumber);
    void setFloor(Integer floor);
    void setCapacity(Integer capacity);
    void setType(String type);
    void setPrice(BigDecimal price);
    void setReservations(Set<Reservation> reservations);
}