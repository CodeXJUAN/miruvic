package cat.uvic.teknos.dam.miruvic.jdbc.repositories;

import java.sql.*;
import java.util.*;
import java.io.*;
import cat.uvic.teknos.dam.miruvic.Student;
import cat.uvic.teknos.dam.miruvic.impl.StudentImpl;
import cat.uvic.teknos.dam.miruvic.StudentRepository;

public class JdbcStudentRepository implements StudentRepository<Student> {
    private Connection getConnection() throws SQLException {
        var properties = new Properties();
        try {
            properties.load(new FileInputStream("datasoruce.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String driver = properties.getProperty("driver");
        String server = properties.getProperty("server");
        String database = properties.getProperty("database");
        String username = properties.getProperty("username");
        String password = properties.getProperty("password");
        return DriverManager.getConnection(String.format("jdbc:%s://%s/%s", driver, server, database),
                username, password);
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
            stmt.setInt(6, student.getAddress());
            if (student.getId() != 0) {
                stmt.setInt(4, student.getId());
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
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }

    @Override
    public Student getId(int id) {
        String sql = "SELECT * FROM STUDENT WHERE id = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Student student = new Student();
                    student.setId(rs.getInt("id"));
                    student.setFirstName(rs.getString("first_name"));
                    student.setLastName(rs.getString("last_name"));
                    student.setEmail(rs.getString("email"));
                    student.setPasswordHash(rs.getString("password_hash"));
                    student.setPhoneNumber(rs.getString("phone_number"));
                    student.setAddress(rs.getInt("address_id"));
                    return student;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
                Student student = new Student();
                student.setId(rs.getInt("id"));
                student.setFirstName(rs.getString("name"));
                student.setLastName(rs.getString("surname"));
                student.setEmail(rs.getString("email"));
                student.setPasswordHash(rs.getString("password_hash"));
                student.setPhoneNumber(rs.getString("phone_number"));
                student.setAddress(rs.getInt("address_id"));
                students.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }
}