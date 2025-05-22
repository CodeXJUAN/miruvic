package cat.uvic.teknos.dam.miruvic.jdbc.repositories;

import java.sql.*;
import java.util.*;
import java.io.*;
import cat.uvic.teknos.dam.miruvic.model.Room;
import cat.uvic.teknos.dam.miruvic.model.impl.RoomImpl;
import cat.uvic.teknos.dam.miruvic.repositories.RoomRepository;

public class JdbcRoomRepository implements RoomRepository<Room> {

    private Connection getConnection() throws cat.uvic.teknos.dam.miruvic.jdbc.exceptions.DataSourceException {
        var properties = new Properties();
        try {
            properties.load(new FileInputStream("datasource.properties"));
        } catch (IOException e) {
            throw new cat.uvic.teknos.dam.miruvic.jdbc.exceptions.DataSourceException("Error al cargar el archivo de propiedades", e);
        }
        String driver = properties.getProperty("driver");
        String server = properties.getProperty("server");
        String database = properties.getProperty("database");
        String username = properties.getProperty("username");
        String password = properties.getProperty("password");
        try {
            return DriverManager.getConnection(String.format("jdbc:%s://%s/%s", driver, server, database),
                    username, password);
        } catch (SQLException e) {
            throw new cat.uvic.teknos.dam.miruvic.jdbc.exceptions.DataSourceException("Error al conectar con la base de datos", e);
        }
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
            throw cat.uvic.teknos.dam.miruvic.jdbc.exceptions.ExceptionUtils.convertSQLException("Error al guardar la habitación", e);
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
            throw cat.uvic.teknos.dam.miruvic.jdbc.exceptions.ExceptionUtils.convertSQLException("Error al eliminar la habitación", e);
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
            throw cat.uvic.teknos.dam.miruvic.jdbc.exceptions.ExceptionUtils.convertSQLException("Error al obtener la habitación", e);
        }
        throw cat.uvic.teknos.dam.miruvic.jdbc.exceptions.ExceptionUtils.createEntityNotFoundException("Habitación", id);
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
            throw cat.uvic.teknos.dam.miruvic.jdbc.exceptions.ExceptionUtils.convertSQLException("Error al obtener todas las habitaciones", e);
        }
        return rooms;
    }
}