package cat.uvic.teknos.dam.miruvic.model.impl;

import cat.uvic.teknos.dam.miruvic.model.Room;
import java.math.BigDecimal;

public class RoomImpl implements Room {
    private int id;
    private String roomNumber;
    private int floor;
    private int capacity;
    private RoomType type;
    private BigDecimal price;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getRoomNumber() {
        return roomNumber;
    }

    @Override
    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    @Override
    public int getFloor() {
        return floor;
    }

    @Override
    public void setFloor(int floor) {
        this.floor = floor;
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public RoomType getType() {
        return type;
    }

    @Override
    public void setType(RoomType type) {
        this.type = type;
    }

    @Override
    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
