package cat.uvic.teknos.dam.miruvic.jdbc.repositories;

import java.sql.*;
import java.util.*;

import cat.uvic.teknos.dam.miruvic.model.Room;
import cat.uvic.teknos.dam.miruvic.model.impl.RoomImpl;
import cat.uvic.teknos.dam.miruvic.repositories.RoomRepository;
import cat.uvic.teknos.dam.miruvic.jdbc.datasources.DataSource;
import cat.uvic.teknos.dam.miruvic.jdbc.exceptions.*;

public class JdbcRoomRepository implements RoomRepository {

    private final DataSource dataSource;

    public JdbcRoomRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void save(Room room) {
        String sql;
        boolean isInsert = room.getId() == null || room.getId() == 0;
        if (isInsert) {
            sql = "INSERT INTO ROOM (room_number, floor, capacity, room_type, price) VALUES (?, ?, ?, ?, ?)";
        } else {
            sql = "UPDATE ROOM SET room_number = ?, floor = ?, capacity = ?, room_type = ?, price = ? WHERE id = ?";
        }

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, room.getRoomNumber());
            stmt.setInt(2, room.getFloor());
            stmt.setInt(3, room.getCapacity());
            stmt.setString(4, room.getType());
            stmt.setBigDecimal(5, room.getPrice());

            if (!isInsert) {
                stmt.setInt(6, room.getId());
            }

            stmt.executeUpdate();

            if (isInsert) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        room.setId(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error al guardar la habitación", e);
        }
    }

    @Override
    public void delete(Room room) {
        String checkSql = "SELECT COUNT(*) FROM RESERVATION WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setInt(1, room.getId());

            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new RepositoryException("No se puede eliminar la habitación porque está asociada a reservas");
                }
            }

            String deleteSql = "DELETE FROM ROOM WHERE id = ?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setInt(1, room.getId());
                deleteStmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error al eliminar la habitación", e);
        }
    }

    @Override
    public Room get(Integer id) {
        String sql = "SELECT * FROM ROOM WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
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

        try (Connection conn = dataSource.getConnection();
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
    public List<Room> findByNumber(String number) {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM ROOM WHERE room_number = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, number);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    rooms.add(mapResultSetToRoom(rs));
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error al buscar habitación por número", e);
        }
        return rooms;
    }

    @Override
    public List<Room> findByType(String type) {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM ROOM WHERE room_type = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, type);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    rooms.add(mapResultSetToRoom(rs));
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error al buscar habitación por tipo", e);
        }
        return rooms;
    }

    private Room mapResultSetToRoom(ResultSet rs) throws SQLException {
        Room room = new RoomImpl();
        try {
            room.setId(rs.getInt("id"));
            room.setRoomNumber(rs.getString("room_number"));
            room.setFloor(rs.getInt("floor"));
            room.setCapacity(rs.getInt("capacity"));
            room.setType(rs.getString("room_type"));
            room.setPrice(rs.getBigDecimal("price"));
        } catch (SQLException e) {
            throw new RepositoryException("Error mapping room", e);
        }
        return room;
    }
}