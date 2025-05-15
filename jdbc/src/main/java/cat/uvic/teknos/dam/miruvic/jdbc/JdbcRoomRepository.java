package cat.uvic.teknos.dam.miruvic.jdbc;

import java.sql.*;
import java.util.*;
import cat.uvic.teknos.dam.miruvic.Room;
import cat.uvic.teknos.dam.miruvic.impl.RoomImpl;
import cat.uvic.teknos.dam.miruvic.RoomRepository;

public class JdbcRoomRepository implements RoomRepository {

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/RUVIC", "root", "rootpassword");
    }

    @Override
    public void save(Room value) {
        String sql;
        if (value.getId() == 0) {
            sql = "INSERT INTO ROOM (room_number, room_type, price) VALUES (?, ?, ?)";
        } else {
            sql = "UPDATE ROOM SET room_number = ?, room_type = ?, price = ? WHERE id_room = ?";
        }

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, value.getRoomNumber());
            stmt.setString(2, value.getRoomType());
            stmt.setDouble(3, value.getPrice());

            if (value.getId() != 0) {
                stmt.setInt(4, value.getId());
            }

            stmt.executeUpdate();

            if (value.getId() == 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        value.setId(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving room", e);
        }
    }

    @Override
    public void delete(Room value) {
        String sql = "DELETE FROM ROOM WHERE id_room = ?";

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, value.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting room", e);
        }
    }

    @Override
    public Room get(Integer id) {
        String sql = "SELECT * FROM ROOM WHERE id_room = ?";

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Room room = new RoomImpl();
                    room.setId(rs.getInt("id_room"));
                    room.setRoomNumber(rs.getString("room_number"));
                    room.setRoomType(rs.getString("room_type"));
                    room.setPrice(rs.getDouble("price"));
                    return room;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting room", e);
        }
        return null;
    }

    @Override
    public Set<Room> getAll() {
        Set<Room> rooms = new HashSet<>();
        String sql = "SELECT * FROM ROOM";

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Room room = new RoomImpl();
                room.setId(rs.getInt("id_room"));
                room.setRoomNumber(rs.getString("room_number"));
                room.setRoomType(rs.getString("room_type"));
                room.setPrice(rs.getDouble("price"));
                rooms.add(room);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting all rooms", e);
        }
        return rooms;
    }
}