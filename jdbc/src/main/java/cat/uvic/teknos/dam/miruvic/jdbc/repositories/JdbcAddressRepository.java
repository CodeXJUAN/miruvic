package cat.uvic.teknos.dam.miruvic.jdbc.repositories;

import java.sql.*;
import java.util.*;
import cat.uvic.teknos.dam.miruvic.model.Address;
import cat.uvic.teknos.dam.miruvic.repositories.AddressRepository;
import cat.uvic.teknos.dam.miruvic.model.impl.AddressImpl;
import cat.uvic.teknos.dam.miruvic.jdbc.exceptions.*;
import cat.uvic.teknos.dam.miruvic.jdbc.datasources.DataSource;

public class JdbcAddressRepository implements AddressRepository {

    private final DataSource dataSource;

    public JdbcAddressRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void save(Address value) {
        String sql;
        if (value.getId() == 0) {
            sql = "INSERT INTO ADDRESS (street, city, state, zip_code, country) VALUES (?, ?, ?, ?, ?)";
        } else {
            sql = "UPDATE ADDRESS SET street = ?, city = ?, state = ?, zip_code = ?, country = ? WHERE id = ?";
        }

        try (Connection conn = dataSource.getConnection();
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
            throw new DataSourceException("Error saving address", e);
        }
    }

    @Override
    public void delete(Address value) {
        String sql = "DELETE FROM ADDRESS WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, value.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException("Error deleting address", e);
        }
    }

    @Override
    public Address get(Integer id) {
        Address address = new AddressImpl();
        String sql = "SELECT * FROM ADDRESS WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    address = mapResultSetToAddress(rs);
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error getting address", e);
        }
        return address;
    }

    @Override
    public Set<Address> getAll() {
        Set<Address> addresses = new HashSet<>();
        String sql = "SELECT * FROM ADDRESS";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                addresses.add(mapResultSetToAddress(rs));
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error getting all addresses", e);
        }
        return addresses;
    }

    private Address mapResultSetToAddress(ResultSet rs) throws SQLException {
        Address address = new AddressImpl();
        address.setId(rs.getInt("id"));
        address.setStreet(rs.getString("street"));
        address.setCity(rs.getString("city"));
        address.setState(rs.getString("state"));
        address.setZipCode(rs.getString("zip_code"));
        address.setCountry(rs.getString("country"));
        return address;
    }

    @Override
    public List<Address> findByCity(String city) {
        List<Address> addresses = new ArrayList<>();
        String sql = "SELECT * FROM ADDRESS WHERE city = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, city);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    addresses.add(mapResultSetToAddress(rs));
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error finding address by city", e);
        }
        return addresses;
    }

    @Override
    public List<Address> findByPostalCode(String postalCode) {
        List<Address> addresses = new ArrayList<>();
        String sql = "SELECT * FROM ADDRESS WHERE zip_code = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, postalCode);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    addresses.add(mapResultSetToAddress(rs));
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error finding address by postal code", e);
        }
        return addresses;
    }

    @Override
    public List<Address> findByCountry(String country) {
        List<Address> addresses = new ArrayList<>();
        String sql = "SELECT * FROM ADDRESS WHERE country = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, country);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    addresses.add(mapResultSetToAddress(rs));
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error finding address by country", e);
        }
        return addresses;
    }
}