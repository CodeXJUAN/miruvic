package cat.uvic.teknos.dam.ruvic;

import java.math.BigDecimal;

public class Room {
    private int id;
    private String roomNumber;
    private int floor;
    private int capacity;
    private RoomType type; // Enum para Individual, Double, Suite
    private BigDecimal price; // Cambiado de double a BigDecimal para decimal(6,2)

    // Enum para los tipos de habitación
    public enum RoomType {
        Individual, Double, Suite
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public RoomType getType() {
        return type;
    }

    public void setType(RoomType type) {
        this.type = type;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}