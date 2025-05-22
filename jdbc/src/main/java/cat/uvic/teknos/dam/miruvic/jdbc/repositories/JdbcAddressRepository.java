package cat.uvic.teknos.dam.miruvic.jdbc.repositories;

import java.sql.*;
import java.util.*;
import java.io.*;
import cat.uvic.teknos.dam.miruvic.Address;
import cat.uvic.teknos.dam.miruvic.AddressRepository;
import cat.uvic.teknos.dam.miruvic.impl.AddressImpl;

public class JdbcAddressRepository implements AddressRepository<Address> {

    private Connection getConnection() throws SQLException {
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
        return DriverManager.getConnection(String.format("jdbc:%s://%s/%s", driver, server, database),
                username, password);
    }

    @Override
    public void save(Address value) {
        String sql;
        if (value.getId() == 0) {
            sql = "INSERT INTO ADDRESS (street, city, state, zip_code, country) VALUES (?, ?, ?, ?, ?)";
        } else {
            sql = "UPDATE ADDRESS SET street = ?, city = ?, state = ?, zip_code = ?, country = ? WHERE id_addresses = ?";
        }
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, value.getStreet());
            stmt.setString(2, value.getCity());
            stmt.setString(3, value.getState());
            stmt.setString(4, value.getZipCode());
            stmt.setString(5, value.getCountry());
            
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
            throw new RuntimeException("Error saving address", e);
        }
    }

    @Override
    public void delete(Address value) {
        String sql = "DELETE FROM ADDRESS WHERE id_addresses = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, value.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting address", e);
        }
    }

    @Override
    public Address get(Integer id) {
        String sql = "SELECT * FROM ADDRESS WHERE id_addresses = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Address address = new AddressImpl();
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
            throw new RuntimeException("Error getting address", e);
        }
        return null;
    }

    @Override
    public Set<Address> getAll() {
        Set<Address> addresses = new HashSet<>();
        String sql = "SELECT * FROM ADDRESS";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Address address = new AddressImpl();
                address.setId(rs.getInt("id_addresses"));
                address.setStreet(rs.getString("street"));
                address.setCity(rs.getString("city"));
                address.setState(rs.getString("state"));
                address.setZipCode(rs.getString("zip_code"));
                address.setCountry(rs.getString("country"));
                addresses.add(address);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting all addresses", e);
        }
        return addresses;
    }

    @Override
    public List<Address> findByCity(String city) {
        List<Address> addresses = new ArrayList<>();
        String sql = "SELECT * FROM ADDRESS WHERE city = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, city);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Address address = new AddressImpl();
                    address.setId(rs.getInt("id_addresses"));
                    address.setStreet(rs.getString("street"));
                    address.setCity(rs.getString("city"));
                    address.setState(rs.getString("state"));
                    address.setZipCode(rs.getString("zip_code"));
                    address.setCountry(rs.getString("country"));
                    addresses.add(address);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding addresses by city", e);
        }
        return addresses;
    }

    @Override
    public List<Address> findByPostalCode(String postalCode) {
        List<Address> addresses = new ArrayList<>();
        String sql = "SELECT * FROM ADDRESS WHERE zip_code = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, postalCode);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Address address = new AddressImpl();
                    address.setId(rs.getInt("id_addresses"));
                    address.setStreet(rs.getString("street"));
                    address.setCity(rs.getString("city"));
                    address.setState(rs.getString("state"));
                    address.setZipCode(rs.getString("zip_code"));
                    address.setCountry(rs.getString("country"));
                    addresses.add(address);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding addresses by postal code", e);
        }
        return addresses;
    }

    @Override
    public List<Address> findByCountry(String country) {
        List<Address> addresses = new ArrayList<>();
        String sql = "SELECT * FROM ADDRESS WHERE country = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, country);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Address address = new AddressImpl();
                    address.setId(rs.getInt("id_addresses"));
                    address.setStreet(rs.getString("street"));
                    address.setCity(rs.getString("city"));
                    address.setState(rs.getString("state"));
                    address.setZipCode(rs.getString("zip_code"));
                    address.setCountry(rs.getString("country"));
                    addresses.add(address);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding addresses by country", e);
        }
        return addresses;
    }
}