package cat.uvic.teknos.dam.miruvic.repositories.jdbc;

import cat.uvic.teknos.dam.miruvic.model.Address;
import cat.uvic.teknos.dam.miruvic.model.Student;
import cat.uvic.teknos.dam.miruvic.model.impl.AddressImpl;
import cat.uvic.teknos.dam.miruvic.model.impl.StudentImpl;
import cat.uvic.teknos.dam.miruvic.jdbc.datasources.DataSource;
import cat.uvic.teknos.dam.miruvic.jdbc.repositories.JdbcAddressRepository;
import cat.uvic.teknos.dam.miruvic.jdbc.repositories.JdbcStudentRepository;
import org.junit.jupiter.api.*;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class JdbcStudentRepositoryIT {
    private static DataSource dataSource;
    private static JdbcStudentRepository studentRepository;
    private static JdbcAddressRepository addressRepository; // For managing addresses

    @BeforeAll
    static void setup() {
        studentRepository = new JdbcStudentRepository(dataSource);
        addressRepository = new JdbcAddressRepository(dataSource);

        // Initialize test database
        try (var conn = dataSource.getConnection();
             var stmt = conn.createStatement()) {
            // Drop tables in reverse order of creation due to FK constraints
            stmt.execute("DROP TABLE IF EXISTS STUDENT");
            stmt.execute("DROP TABLE IF EXISTS ADDRESS");

            stmt.execute("CREATE TABLE IF NOT EXISTS ADDRESS (" +
                    "id_addresses INT PRIMARY KEY AUTO_INCREMENT, " +
                    "street VARCHAR(100), " +
                    "city VARCHAR(50), " +
                    "state VARCHAR(50), " +
                    "zip_code VARCHAR(20), " +
                    "country VARCHAR(50))");

            stmt.execute("CREATE TABLE IF NOT EXISTS STUDENT (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "first_name VARCHAR(100), " +
                    "last_name VARCHAR(100), " +
                    "email VARCHAR(100) UNIQUE, " +
                    "password_hash VARCHAR(255), " +
                    "phone_number VARCHAR(20), " +
                    "address_id INT, " +
                    "FOREIGN KEY (address_id) REFERENCES ADDRESS(id_addresses))");

        } catch (Exception e) {
            throw new RuntimeException("Test DB setup failed", e);
        }
    }

    @AfterEach
    void cleanUp() {
        try (var conn = dataSource.getConnection();
             var stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM STUDENT");
            stmt.execute("DELETE FROM ADDRESS"); // Also clean addresses if they were created for students
        } catch (Exception e) {
            throw new RuntimeException("Test cleanup failed", e);
        }
    }

    private Address createAndSaveTestAddress(String street, String city) {
        Address address = new AddressImpl();
        address.setStreet(street);
        address.setCity(city);
        address.setState("Test State");
        address.setZipCode("12345");
        address.setCountry("Test Country");
        addressRepository.save(address);
        return address;
    }

    private Student createTestStudent(String firstName, String email, Address address) {
        Student student = new StudentImpl();
        student.setFirstName(firstName);
        student.setLastName("TestLastName");
        student.setEmail(email);
        student.setPasswordHash("testPassword");
        student.setPhoneNumber("123456789");
        if (address != null) {
            student.setAddress(address);
        }
        return student;
    }

    @Test
    void shouldSaveNewStudent() {
        Address address = createAndSaveTestAddress("1 Main St", "Testville");
        Student student = createTestStudent("John", "john.new@example.com", address);

        studentRepository.save(student);

        assertTrue(student.getId() > 0, "Student should have ID after save");
        Student saved = studentRepository.get(student.getId());
        assertNotNull(saved);
        assertEquals("John", saved.getFirstName());
        assertNotNull(saved.getAddress());
        assertEquals(address.getId(), saved.getAddress());
    }

    @Test
    void shouldUpdateExistingStudent() {
        Address address = createAndSaveTestAddress("2 Oak Ave", "UpdateCity");
        Student student = createTestStudent("Jane", "jane.update@example.com", address);
        studentRepository.save(student);

        student.setFirstName("Jane Updated");
        student.setEmail("jane.updated.email@example.com");
        studentRepository.save(student);

        Student updated = studentRepository.get(student.getId());
        assertEquals("Jane Updated", updated.getFirstName());
        assertEquals("jane.updated.email@example.com", updated.getEmail());
    }

    @Test
    void shouldDeleteStudent() {
        Address address = createAndSaveTestAddress("3 Pine Ln", "DeleteTown");
        Student student = createTestStudent("Mike", "mike.delete@example.com", address);
        studentRepository.save(student);

        studentRepository.delete(student);

        assertThrows(cat.uvic.teknos.dam.miruvic.jdbc.exceptions.EntityNotFoundException.class, () -> {
            studentRepository.get(student.getId());
        });
    }

    @Test
    void shouldGetStudentById() {
        Address address = createAndSaveTestAddress("4 Elm Rd", "GetCity");
        Student student = createTestStudent("Sarah", "sarah.get@example.com", address);
        studentRepository.save(student);

        Student found = studentRepository.get(student.getId());

        assertNotNull(found);
        assertEquals(student.getFirstName(), found.getFirstName());
    }

    @Test
    void shouldGetAllStudents() {
        Address address1 = createAndSaveTestAddress("5 Maple Dr", "AllCity1");
        studentRepository.save(createTestStudent("StudentA", "student.a@example.com", address1));
        Address address2 = createAndSaveTestAddress("6 Birch St", "AllCity2");
        studentRepository.save(createTestStudent("StudentB", "student.b@example.com", address2));

        Set<Student> students = studentRepository.getAll();

        assertEquals(2, students.size());
    }

    @Test
    void shouldFindStudentByName() {
        Address address = createAndSaveTestAddress("7 Cedar Ct", "FindNameCity");
        studentRepository.save(createTestStudent("UniqueName", "uniquename@example.com", address));
        studentRepository.save(createTestStudent("AnotherName", "anothername@example.com", null));

        Student found = studentRepository.findByName("UniqueName");
        assertNotNull(found);
        assertEquals("UniqueName", found.getFirstName());

        Student foundPartial = studentRepository.findByName("Name"); // Should find either or based on LIKE %Name%
        assertNotNull(foundPartial);
    }

    @Test
    void shouldFindStudentByEmail() {
        Address address = createAndSaveTestAddress("8 Willow Way", "FindEmailCity");
        studentRepository.save(createTestStudent("EmailUser", "user.email.specific@example.com", address));

        Student found = studentRepository.findByEmail("user.email.specific@example.com");
        assertNotNull(found);
        assertEquals("user.email.specific@example.com", found.getEmail());
    }

    @Test
    void shouldFindStudentByPhone() {
        Address address = createAndSaveTestAddress("9 Spruce Pl", "FindPhoneCity");
        studentRepository.save(createTestStudent("PhoneUser", "phone.user@example.com", address));
        // Assuming createTestStudent sets a default phone number "123456789"

        Student found = studentRepository.findByPhone("123456789");
        assertNotNull(found);
        assertEquals("123456789", found.getPhoneNumber());
    }
}