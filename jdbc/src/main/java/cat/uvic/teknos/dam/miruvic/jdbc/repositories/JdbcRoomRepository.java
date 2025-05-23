package cat.uvic.teknos.dam.miruvic.jdbc.repositories;

import java.sql.*;
import java.util.*;
import java.io.*;
import cat.uvic.teknos.dam.miruvic.model.Room;
import cat.uvic.teknos.dam.miruvic.model.Room.RoomType;
import cat.uvic.teknos.dam.miruvic.model.impl.RoomImpl;
import cat.uvic.teknos.dam.miruvic.repositories.RoomRepository;
import cat.uvic.teknos.dam.miruvic.jdbc.datasources.DataSource;
import cat.uvic.teknos.dam.miruvic.jdbc.exceptions.*;

public class JdbcRoomRepository implements RoomRepository<Room> {

    private final DataSource dataSource;

    public JdbcRoomRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private Connection getConnection() throws DataSourceException {
        var properties = new Properties();
        try {
            properties.load(new FileInputStream("datasource.properties"));
        } catch (IOException e) {
            throw new DataSourceException("Error al cargar el archivo de propiedades", e);
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
            throw new DataSourceException("Error al conectar con la base de datos", e);
        }
    }

    @Override
    public void save(Room value) {
        String sql;
        if (value.getId() == 0) {
            sql = "INSERT INTO ROOM (room_number, floor, capacity, room_type, price) VALUES (?, ?, ?, ?, ?)";
        } else {
            sql = "UPDATE ROOM SET room_number = ?, floor = ?, capacity = ?, room_type = ?, price = ? WHERE id_room = ?";
        }

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, value.getRoomNumber());
            stmt.setInt(2, value.getFloor());
            stmt.setInt(3, value.getCapacity());
            stmt.setString(4, value.getType().toString());
            stmt.setBigDecimal(5, value.getPrice());

            if (value.getId() != 0) {
                stmt.setInt(6, value.getId());
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
            throw new RepositoryException("Error al guardar la habitación", e);
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
            throw new RepositoryException("Error al eliminar la habitación", e);
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
                    return mapResultSetToRoom(rs);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error al obtener la habitación", e);
        }
        throw new EntityNotFoundException("Habitación no encontrada con id: " + id);
    }

    @Override
    public Set<Room> getAll() {
        Set<Room> rooms = new HashSet<>();
        String sql = "SELECT * FROM ROOM";

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                rooms.add(mapResultSetToRoom(rs));
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error al obtener todas las habitaciones", e);
        }
        return rooms;
    }
    
    @Override
    public Room findByNumber(String number) {
        String sql = "SELECT * FROM ROOM WHERE room_number = ?";

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, number);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRoom(rs);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error al buscar habitación por número", e);
        }
        throw new EntityNotFoundException("Habitación no encontrada con número: " + number);
    }

    @Override
    public Room findByType(String type) {
        String sql = "SELECT * FROM ROOM WHERE room_type = ?";

        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, type);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRoom(rs);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error al buscar habitación por tipo", e);
        }
        throw new EntityNotFoundException("Habitación no encontrada con tipo: " + type);
    }
    
    private Room mapResultSetToRoom(ResultSet rs) {
        try {
            Room room = new RoomImpl();
            room.setId(rs.getInt("id_room"));
            room.setRoomNumber(rs.getString("room_number"));
            room.setFloor(rs.getInt("floor"));
            room.setCapacity(rs.getInt("capacity"));
            room.setType(RoomType.valueOf(rs.getString("room_type")));
            room.setPrice(rs.getBigDecimal("price"));
            return room;
        } catch (SQLException e) {
            throw new RepositoryException("Error al mapear los datos de la habitación", e);
        }
    }
}