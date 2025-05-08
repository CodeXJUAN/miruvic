package cat.uvic.teknos.dam.miruvic;

public interface RoomRepository<Room> extends Repository<Integer, Room> {
    Room findByNumber(String number);

    java.util.List<Room> findByType(String type);
}