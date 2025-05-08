package cat.uvic.teknos.dam.miruvic;

import java.math.BigDecimal;

public interface Room {

    int getId();

    void setId(int id);

    String getRoomNumber();

    void setRoomNumber(String roomNumber);

    int getFloor();

    void setFloor(int floor);

    int getCapacity();

    void setCapacity(int capacity);

    RoomType getType();

    void setType(RoomType type);

    BigDecimal getPrice();

    void setPrice(BigDecimal price);

    enum RoomType {
        Individual, Double, Suite
    }
}