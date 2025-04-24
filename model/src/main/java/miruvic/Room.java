package miruvic;

import java.math.BigDecimal;

public interface Room {
    // Enum para los tipos de habitación
    enum RoomType {
        Individual, Double, Suite
    }

    // Métodos de la interfaz
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
}