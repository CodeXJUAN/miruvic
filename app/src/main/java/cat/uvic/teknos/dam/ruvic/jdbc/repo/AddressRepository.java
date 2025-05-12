package cat.uvic.teknos.dam.ruvic.jdbc.repo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import cat.uvic.teknos.dam.ruvic.jdbc.classes.Address;

public class AddressRepository {
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/RUVIC", "root", "rootpassword");
    }

    public void save(Address address) {
        String sql;
        if (address.getId() == 0) {
            sql = "INSERT INTO ADDRESS (street, city, state, zip_code, country) VALUES (?, ?, ?, ?, ?)";
        } else {
            sql = "UPDATE ADDRESS SET street = ?, city = ?, state = ?, zip_code = ?, country = ? WHERE id_addresses = ?";
        }
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, address.getStreet());
            stmt.setString(2, address.getCity());
            stmt.setString(3, address.getState());
            stmt.setString(4, address.getZipCode());
            stmt.setString(5, address.getCountry());
            if (address.getId() != 0) {
                stmt.setInt(6, address.getId());
            }
            stmt.executeUpdate();
            if (address.getId() == 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        address.setId(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(Address address) {
        String sql = "DELETE FROM ADDRESS WHERE id_addresses = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, address.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Address get(int id) {
        String sql = "SELECT * FROM ADDRESS WHERE id_addresses = ?";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Address address = new Address();
                    address.setId(rs.getInt("id_addresses"));
                    address.setStreet(rs.getString("street"));
                    address.setCity(rs.getString("city"));
                    address.setState(rs.getString("state"));
                    address.setZipCode(rs.getString("zip_code"));
                    address.setCountry(rs.getString("country"));
                    return address;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Address> getAll() {
        List<Address> addresses = new ArrayList<>();
        String sql = "SELECT * FROM ADDRESS";
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Address address = new Address();
                address.setId(rs.getInt("id_addresses"));
                address.setStreet(rs.getString("street"));
                address.setCity(rs.getString("city"));
                address.setState(rs.getString("state"));
                address.setZipCode(rs.getString("zip_code"));
                address.setCountry(rs.getString("country"));
                addresses.add(address);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return addresses;
    }
}