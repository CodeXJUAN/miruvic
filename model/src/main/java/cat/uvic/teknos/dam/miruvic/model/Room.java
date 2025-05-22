package cat.uvic.teknos.dam.miruvic.model;

import java.math.BigDecimal;

public interface Room {

    public int getId();

    public void setId(int id);

    public String getRoomNumber();

    public void setRoomNumber(String roomNumber);

    public int getFloor();

    public void setFloor(int floor);

    public int getCapacity();

    public void setCapacity(int capacity);

    public RoomType getType();

    public void setType(RoomType type);

    public BigDecimal getPrice();

    public void setPrice(BigDecimal price);

    public enum RoomType {
        Individual, Double, Suite
    }
}