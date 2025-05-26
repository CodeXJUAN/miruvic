package cat.uvic.teknos.dam.miruvic.jdbc.repositories;

import java.sql.*;
import java.util.*;
import cat.uvic.teknos.dam.miruvic.model.Student;
import cat.uvic.teknos.dam.miruvic.model.Address;
import cat.uvic.teknos.dam.miruvic.model.impl.StudentImpl;
import cat.uvic.teknos.dam.miruvic.model.impl.AddressImpl;
import cat.uvic.teknos.dam.miruvic.repositories.StudentRepository;
import cat.uvic.teknos.dam.miruvic.jdbc.datasources.DataSource;
import cat.uvic.teknos.dam.miruvic.jdbc.exceptions.*;

public class JdbcStudentRepository implements StudentRepository<Student> {
    
    private final DataSource dataSource;

    public JdbcStudentRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void save(Student student) {
        String sql;
        if (student.getId() == 0) {
            sql = "INSERT INTO STUDENT (first_name, last_name, email, password_hash, phone_number, address_id) VALUES (?, ?, ?, ?, ?, ?)";
        } else {
            sql = "UPDATE STUDENT SET first_name = ?, last_name = ?, email = ?, password_hash = ?, phone_number = ?, address_id = ? WHERE id = ?";
        }
        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, student.getFirstName());
            stmt.setString(2, student.getLastName());
            stmt.setString(3, student.getEmail());
            stmt.setString(4, student.getPasswordHash());
            stmt.setString(5, student.getPhoneNumber());
            
            // Obtener el ID de la dirección si existe
            if (student.getAddress() != null) {
                stmt.setInt(6, ((Student) student.getAddress()).getId());
            } else {
                stmt.setNull(6, java.sql.Types.INTEGER);
            }
            
            if (student.getId() != 0) {
                stmt.setInt(7, student.getId());
            }
            stmt.executeUpdate();
            if (student.getId() == 0) {
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
        student.setId(rs.getInt("id"));
        student.setFirstName(rs.getString("first_name"));
        student.setLastName(rs.getString("last_name"));
        student.setEmail(rs.getString("email"));
        student.setPasswordHash(rs.getString("password_hash"));
        student.setPhoneNumber(rs.getString("phone_number"));
        
        // Crear objeto Address si hay un address_id
        int addressId = rs.getInt("address_id");
        if (!rs.wasNull()) {
            Address address = new AddressImpl();
            address.setId(addressId);
            student.setAddress(address);
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
    public Student findByName(String name) {
        String sql = "SELECT * FROM STUDENT WHERE first_name LIKE ? OR last_name LIKE ? LIMIT 1";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            String searchPattern = "%" + name + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStudent(rs);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error al buscar estudiante por nombre", e);
        }
        return null;
    }
    
    @Override
    public Student findByEmail(String email) {
        String sql = "SELECT * FROM STUDENT WHERE email LIKE ? LIMIT 1";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            String searchPattern = "%" + email + "%";
            stmt.setString(1, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStudent(rs);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error al buscar estudiante por email", e);
        }
        return null;
    }
    
    @Override
    public Student findByPhone(String phone_number) {
        String sql = "SELECT * FROM STUDENT WHERE phone_number LIKE ? LIMIT 1";
        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            String searchPattern = "%" + phone_number + "%";
            stmt.setString(1, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStudent(rs);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error al buscar estudiante por telefono", e);
        }
        return null;
    }
}