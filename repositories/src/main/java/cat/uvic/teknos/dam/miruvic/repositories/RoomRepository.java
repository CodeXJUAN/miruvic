package cat.uvic.teknos.dam.miruvic.repositories;

import cat.uvic.teknos.dam.miruvic.model.Room;

import java.util.List;

public interface RoomRepository extends Repository<Integer, Room> {
    List<Room> findByNumber(String number);

    List<Room> findByType(String type);
}