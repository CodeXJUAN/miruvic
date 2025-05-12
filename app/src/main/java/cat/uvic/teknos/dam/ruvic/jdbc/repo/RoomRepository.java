package cat.uvic.teknos.dam.ruvic.jdbc.repo;

import java.util.List;
import java.sql.*;
import java.util.ArrayList;
import cat.uvic.teknos.dam.ruvic.jdbc.classes.Room;

public class RoomRepository {
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/RUVIC", "root", "rootpassword");
    }

    public void save(Room room) {
        String sql;
        if (room.getId() == 0) {
            sql = "INSERT INTO ROOM (room_number, room_type, price_per_night) VALUES (?, ?, ?)";
        } else {
            sql = "UPDATE ROOM SET room_number = ?, room_type = ?, price_per_night = ? WHERE id = ?";
        }
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, room.getRoomNumber());
            stmt.setString(2, room.getType());
            stmt.setBigDecimal(3, room.getPrice());
            if (room.getId() != 0) {
                stmt.setInt(4, room.getId());
            }
            stmt.executeUpdate();
            if (room.getId() == 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        room.setId(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(Room room) {
        String sql = "DELETE FROM ROOM WHERE id = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, room.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Room get(int id) {
        String sql = "SELECT * FROM ROOM WHERE id = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Room room = new Room();
                    room.setId(rs.getInt("id"));
                    room.setRoomNumber(rs.getString("room_number"));
                    room.setType(rs.getString("room_type"));
                    room.setPrice(rs.getBigDecimal("price_per_night"));
                    return room;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Room> getAll() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM ROOM";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Room room = new Room();
                room.setId(rs.getInt("id"));
                room.setRoomNumber(rs.getString("room_number"));
                room.setType(rs.getString("room_type"));
                room.setPrice(rs.getBigDecimal("price_per_night"));
                rooms.add(room);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }
}