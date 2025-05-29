package cat.uvic.teknos.dam.miruvic.repositories;

import cat.uvic.teknos.dam.miruvic.model.Room;

public interface RoomRepository extends Repository<Integer, Room> {
    Room findByNumber(String number);

    Room findByType(String type);
}