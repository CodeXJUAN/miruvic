package cat.uvic.teknos.dam.miruvic.repositories;

public interface RoomRepository<Room> extends Repository<Integer, Room> {
    Room findByNumber(String number);

    Room findByType(String type);
}