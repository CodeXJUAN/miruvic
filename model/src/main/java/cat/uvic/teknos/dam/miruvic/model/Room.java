package cat.uvic.teknos.dam.miruvic.model;

import java.math.BigDecimal;

public interface Room {

    public Integer getId();
    public String getRoomNumber();
    public Integer getFloor();
    public Integer getCapacity();
    public String getType();
    public BigDecimal getPrice();



    public void setId(Integer id);
    public void setRoomNumber(String roomNumber);
    public void setFloor(Integer floor);
    public void setCapacity(Integer capacity);
    public void setType(String type);
    public void setPrice(BigDecimal price);

}