package cat.uvic.teknos.dam.miruvic.jdbc.repositories;

import java.sql.*;
import java.util.*;

import cat.uvic.teknos.dam.miruvic.model.Address;
import cat.uvic.teknos.dam.miruvic.model.Reservation;
import cat.uvic.teknos.dam.miruvic.model.Student;
import cat.uvic.teknos.dam.miruvic.model.impl.ReservationImpl;
import cat.uvic.teknos.dam.miruvic.model.impl.StudentImpl;
import cat.uvic.teknos.dam.miruvic.model.impl.AddressImpl;
import cat.uvic.teknos.dam.miruvic.repositories.StudentRepository;
import cat.uvic.teknos.dam.miruvic.jdbc.datasources.DataSource;
import cat.uvic.teknos.dam.miruvic.jdbc.exceptions.*;

public class JdbcStudentRepository implements StudentRepository {

    private final DataSource dataSource;

    public JdbcStudentRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void save(Student student) {
        String sql;
        boolean isInsert = student.getId() == null || student.getId() == 0;
        if (isInsert) {
            sql = "INSERT INTO STUDENT (first_name, last_name, email, password_hash, phone_number, address_id, reservation_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        } else {
            sql = "UPDATE STUDENT SET first_name = ?, last_name = ?, email = ?, password_hash = ?, phone_number = ?, address_id = ?, reservation_id = ? WHERE id = ?";
        }
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, student.getFirstName());
            stmt.setString(2, student.getLastName());
            stmt.setString(3, student.getEmail());
            stmt.setString(4, student.getPasswordHash());
            stmt.setString(5, student.getPhoneNumber());

            Address address = student.getAddress() != null
                    ? student.getAddress().stream().findFirst().orElse(null)
                    : null;
            if (address != null) {
                stmt.setInt(6, address.getId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }

            Reservation reservation = student.getReservations() != null
                    ? student.getReservations().stream().findFirst().orElse(null)
                    : null;
            if (reservation != null) {
                stmt.setInt(7, reservation.getId());
            } else {
                stmt.setNull(7, Types.INTEGER);
            }

            if (!isInsert) {
                stmt.setInt(8, student.getId());
            }

            stmt.executeUpdate();
            if (isInsert) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        student.setId(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataSourceException("Error saving student", e);
        }
    }

    @Override
    public void delete(Student student) {
        String sql = "DELETE FROM STUDENT WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, student.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Error deleting student", e);
        }
    }

    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        Student student = new StudentImpl();
        try {
            student.setId(rs.getInt("id"));
            student.setFirstName(rs.getString("first_name"));
            student.setLastName(rs.getString("last_name"));
            student.setEmail(rs.getString("email"));
            student.setPasswordHash(rs.getString("password_hash"));
            student.setPhoneNumber(rs.getString("phone_number"));

            int addressId = rs.getInt("address_id");
            if (!rs.wasNull() && addressId != 0) {
                Address address = new AddressImpl();
                address.setId(addressId);
                student.setAddress(Set.of(address));
            }

            int reservationId = rs.getInt("reservation_id");
            if (!rs.wasNull() && reservationId != 0) {
                Reservation reservation = new ReservationImpl();
                reservation.setId(reservationId);
                student.setReservations(Set.of(reservation));
            }

        } catch (SQLException e) {
            throw new RepositoryException("Error mapping Student", e);
        }

        return student;
    }

    @Override
    public Student get(Integer id) {
        String sql = "SELECT * FROM STUDENT WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStudent(rs);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error getting student", e);
        }
        throw new EntityNotFoundException("Student not found with id: " + id);
    }

    @Override
    public Set<Student> getAll() {
        Set<Student> students = new HashSet<>();
        String sql = "SELECT * FROM STUDENT";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error getting all students", e);
        }
        return students;
    }

    @Override
    public List<Student> findByName(String name) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM STUDENT WHERE first_name LIKE ? OR last_name LIKE ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String searchPattern = "%" + name + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    students.add(mapResultSetToStudent(rs));
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error al buscar estudiante por nombre", e);
        }
        return students;
    }

    @Override
    public List<Student> findByEmail(String email) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM STUDENT WHERE email LIKE ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String searchPattern = "%" + email + "%";
            stmt.setString(1, searchPattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    students.add(mapResultSetToStudent(rs));
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error al buscar estudiante por email", e);
        }
        return students;
    }

    @Override
    public List<Student> findByPhone(String phone_number) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM STUDENT WHERE phone_number LIKE ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String searchPattern = "%" + phone_number + "%";
            stmt.setString(1, searchPattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    students.add(mapResultSetToStudent(rs));
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error al buscar estudiante por telefono", e);
        }
        return students;
    }
}