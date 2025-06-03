package cat.uvic.teknos.dam.miruvic.model.impl;

import cat.uvic.teknos.dam.miruvic.model.Reservation;
import cat.uvic.teknos.dam.miruvic.model.Room;
import java.math.BigDecimal;
import java.util.Set;

public class RoomImpl implements Room {
    private Integer id;
    private String roomNumber;
    private Integer floor;
    private Integer capacity;
    private String type;
    private BigDecimal price;
    private Set<Reservation> reservation;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
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
    public Integer getFloor() {
        return floor;
    }

    @Override
    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    @Override
    public Integer getCapacity() {
        return capacity;
    }

    @Override
    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
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

    @Override
    public Set<Reservation> getReservations() {
        return Set.of();
    }

    @Override
    public void setReservations(Set<Reservation> reservations) {
        this.reservation = reservations;
    }
}
