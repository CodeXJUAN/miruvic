package cat.uvic.teknos.dam.miruvic.jdbc.repositories;

import java.sql.*;
import java.util.*;
import java.io.*;
import cat.uvic.teknos.dam.miruvic.model.Student;
import cat.uvic.teknos.dam.miruvic.model.Address;
import cat.uvic.teknos.dam.miruvic.model.impl.StudentImpl;
import cat.uvic.teknos.dam.miruvic.model.impl.AddressImpl;
import cat.uvic.teknos.dam.miruvic.repositories.StudentRepository;
import cat.uvic.teknos.dam.miruvic.jdbc.exceptions.*;

public class JdbcStudentRepository implements StudentRepository<Student> {
    private Connection getConnection() throws DataSourceException {
        var properties = new Properties();
        try {
            properties.load(new FileInputStream("datasource.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
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
                throw new DataSourceException("Error connecting to database", e);
            }
    }

    @Override
    public void save(Student student) {
        String sql;
        if (student.getId() == 0) {
            sql = "INSERT INTO STUDENT (first_name, last_name, email, password_hash, phone_number, address_id) VALUES (?, ?, ?, ?, ?, ?)";
        } else {
            sql = "UPDATE STUDENT SET first_name = ?, last_name = ?, email = ?, password_hash = ?, phone_number = ?, address_id = ? WHERE id = ?";
        }
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, student.getFirstName());
            stmt.setString(2, student.getLastName());
            stmt.setString(3, student.getEmail());
            stmt.setString(4, student.getPasswordHash());
            stmt.setString(5, student.getPhoneNumber());
            
            // Obtener el ID de la dirección si existe
            if (student.getAddress() != null) {
                stmt.setInt(6, student.getAddress().getId());
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
            throw convertSQLException("Error al guardar estudiante", e);
        }
    }

    @Override
    public void delete(Student student) {
        String sql = "DELETE FROM STUDENT WHERE id = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, student.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw convertSQLException("Error al eliminar estudiante", e);
        }
    }

    @Override
    public Student get(Integer id) {
        String sql = "SELECT * FROM STUDENT WHERE id = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
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
            }
        } catch (SQLException e) {
            throw convertSQLException("Error al obtener estudiante", e);
        }
        return null;
    }

    @Override
    public List<Student> getAll() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM STUDENT";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
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
                
                students.add(student);
            }
        } catch (SQLException e) {
            throw convertSQLException("Error al obtener todos los estudiantes", e);
        }
        return students;
    }
    
    @Override
    public List<Student> findByName(String name) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM STUDENT WHERE first_name LIKE ? OR last_name LIKE ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            String searchPattern = "%" + name + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
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
                    
                    students.add(student);
                }
            }
        } catch (SQLException e) {
            throw convertSQLException("Error al buscar estudiantes por nombre", e);
        }
        return students;
    }
    
    @Override
    public List<Student> findByEmail(String email) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM STUDENT WHERE email LIKE ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            String searchPattern = "%" + email + "%";
            stmt.setString(1, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
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
                    
                    students.add(student);
                }
            }
        } catch (SQLException e) {
            throw convertSQLException("Error al buscar estudiantes por email", e);
        }
        return students;
    }
    
    @Override
    public List<Student> findByMatricula(String matricula) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM STUDENT WHERE matricula LIKE ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            String searchPattern = "%" + matricula + "%";
            stmt.setString(1, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
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
                    
                    students.add(student);
                }
            }
        } catch (SQLException e) {
            throw convertSQLException("Error al buscar estudiantes por matrícula", e);
        }
        return students;
    }
}