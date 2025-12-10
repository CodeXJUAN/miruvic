package cat.uvic.teknos.dam.miruvic.repositories.jdbc;

import cat.uvic.teknos.dam.miruvic.model.Address;
import cat.uvic.teknos.dam.miruvic.model.impl.AddressImpl;
import cat.uvic.teknos.dam.miruvic.jdbc.datasources.DataSource;
import cat.uvic.teknos.dam.miruvic.jdbc.repositories.JdbcAddressRepository;
import org.junit.jupiter.api.*;
import cat.uvic.teknos.dam.miruvic.jdbc.datasources.SingleConnectionDataSource;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class JdbcAddressRepositoryIT {
    private static DataSource dataSource;
    private static JdbcAddressRepository repository;

    @BeforeAll
    static void setup() {
        dataSource = new SingleConnectionDataSource(
                "mysql", "localhost:3306", "miruvic_test", "juanm", "teknos"
        );

        repository = new JdbcAddressRepository(dataSource);

        // Initialize test database
        try (var conn = dataSource.getConnection();
             var stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS ADDRESS (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "street VARCHAR(100), " +
                    "city VARCHAR(50), " +
                    "state VARCHAR(50), " +
                    "zip_code VARCHAR(20), " +
                    "country VARCHAR(50))");
            stmt.execute("DELETE FROM ADDRESS");
        } catch (Exception e) {
            throw new RuntimeException("Test DB setup failed", e);
        }
    }

    @AfterEach
    void cleanUp() {
        try (var conn = dataSource.getConnection();
             var stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM ADDRESS");
        } catch (Exception e) {
            throw new RuntimeException("Test cleanup failed", e);
        }
    }

    @Test
    void shouldSaveNewAddress() {
        Address address = new AddressImpl();
        address.setStreet("123 Main St");
        address.setCity("Barcelona");
        address.setState("Catalonia");
        address.setZipCode("08001");
        address.setCountry("Spain");

        repository.save(address);

        assertTrue(address.getId() > 0, "Address should have ID after save");
        Address saved = repository.get(address.getId());
        assertEquals("Barcelona", saved.getCity());
    }

    @Test
    void shouldUpdateExistingAddress() {
        Address address = createTestAddress();
        repository.save(address);

        address.setCity("Updated City");
        repository.save(address);

        Address updated = repository.get(address.getId());
        assertEquals("Updated City", updated.getCity());
    }

    @Test
    void shouldDeleteAddress() {
        Address address = createTestAddress();
        repository.save(address);

        repository.delete(address);

        Address deleted = repository.get(address.getId());
        assertNull(deleted, "Address should be null after deletion");
    }

    @Test
    void shouldGetAddressById() {
        Address address = createTestAddress();
        repository.save(address);

        Address found = repository.get(address.getId());

        assertNotNull(found);
        assertEquals(address.getStreet(), found.getStreet());
    }

    @Test
    void shouldGetAllAddresses() {
        repository.save(createTestAddress("Address 1"));
        repository.save(createTestAddress("Address 2"));

        Set<Address> addresses = repository.getAll();

        assertTrue(addresses.size() >= 2);
    }

    @Test
    void shouldFindByCity() {
        repository.save(createTestAddress("Main St", "Barcelona"));
        repository.save(createTestAddress("Second St", "Madrid"));

        List<Address> found = repository.findByCity("Barcelona");

        assertNotNull(found);
        assertFalse(found.isEmpty());
        assertEquals("Barcelona", found.getFirst().getCity());
    }

    @Test
    void shouldFindByPostalCode() {
        repository.save(createTestAddress("Main St", "Barcelona", "08001"));
        repository.save(createTestAddress("Second St", "Manresa", "08002"));

        List<Address> found = repository.findByPostalCode("08001");

        assertNotNull(found);
        assertFalse(found.isEmpty());
        assertEquals("08001", found.getFirst().getZipCode());
    }

    @Test
    void shouldFindByCountry() {
        repository.save(createTestAddress("Main St", "Barcelona", "12345", "Spain"));
        repository.save(createTestAddress("Second St", "Paris", "54321", "France"));

        List<Address> found = repository.findByCountry("Spain");

        assertNotNull(found);
        assertFalse(found.isEmpty());
        assertEquals("Spain", found.getFirst().getCountry());
    }

    private Address createTestAddress() {
        return createTestAddress("Test Street", "Test City", "12345", "Test Country");
    }

    private Address createTestAddress(String street) {
        return createTestAddress(street, "Test City", "12345", "Test Country");
    }

    private Address createTestAddress(String street, String city) {
        return createTestAddress(street, city, "12345", "Test Country");
    }

    private Address createTestAddress(String street, String city, String zipCode) {
        return createTestAddress(street, city, zipCode, "Test Country");
    }

    private Address createTestAddress(String street, String city, String zipCode, String country) {
        Address address = new AddressImpl();
        address.setStreet(street);
        address.setCity(city);
        address.setState("Test State");
        address.setZipCode(zipCode);
        address.setCountry(country);
        return address;
    }
}