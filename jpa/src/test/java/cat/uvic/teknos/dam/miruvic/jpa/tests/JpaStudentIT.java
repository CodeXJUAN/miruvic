package cat.uvic.teknos.dam.miruvic.jpa.tests;

import cat.uvic.teknos.dam.miruvic.jpa.model.JpaAddress;
import cat.uvic.teknos.dam.miruvic.jpa.model.JpaStudent;
import cat.uvic.teknos.dam.miruvic.jpa.repository.JpaAddressRepository;
import cat.uvic.teknos.dam.miruvic.jpa.repository.JpaStudentRepository;
import cat.uvic.teknos.dam.miruvic.model.Address;
import cat.uvic.teknos.dam.miruvic.model.Student;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class JpaStudentIT {

    private static EntityManager entityManager;
    private static EntityManagerFactory entityManagerFactory;
    private static JpaStudentRepository studentRepository;
    private static JpaAddressRepository addressRepository;
    private static Integer savedStudentId;
    private static Integer savedAddressId;

    @BeforeAll
    public static void setup() {
        entityManagerFactory = Persistence.createEntityManagerFactory("miruvic_test");
        entityManager = entityManagerFactory.createEntityManager();
        studentRepository = new JpaStudentRepository(entityManagerFactory);
        addressRepository = new JpaAddressRepository(entityManagerFactory);
    }

    @AfterAll
    public static void tearDown() {
        if (entityManager != null) {
            entityManager.close();
        }
        if (entityManagerFactory != null) {
            entityManagerFactory.close();
        }
    }

    @Test
    @Order(1)
    public void testSaveStudentWithAddress() {
        entityManager.getTransaction().begin();

        JpaAddress address = new JpaAddress();
        address.setStreet("Calle Estudiante");
        address.setCity("Barcelona");
        address.setState("Cataluña");
        address.setZipCode("08002");
        address.setCountry("España");
        entityManager.persist(address);

        JpaStudent student = new JpaStudent();
        student.setFirstName("Juan");
        student.setLastName("Pérez");
        student.setEmail("juan.perez@example.com");
        student.setPasswordHash("hash789");
        student.setPhoneNumber("123456789");
        student.setAddress(address);
        entityManager.persist(student);

        entityManager.getTransaction().commit();

        assertNotNull(address.getId(), "El ID de la dirección no debería ser nulo después de guardar");
        assertNotNull(student.getId(), "El ID del estudiante no debería ser nulo después de guardar");
        savedAddressId = address.getId();
        savedStudentId = student.getId();
    }

    @Test
    @Order(2)
    public void testGetStudent() {
        Student student = studentRepository.get(savedStudentId);

        assertNotNull(student, "El estudiante no debería ser nulo");
        assertEquals("Juan", student.getFirstName(), "El nombre no coincide");
        assertEquals("Pérez", student.getLastName(), "El apellido no coincide");
        assertEquals("juan.perez@example.com", student.getEmail(), "El email no coincide");
        assertEquals("123456789", student.getPhoneNumber(), "El teléfono no coincide");
        assertNotNull(student.getAddress(), "El estudiante debería tener una dirección asociada");
    }

    @Test
    @Order(3)
    public void testUpdateStudent() {
        Student student = studentRepository.get(savedStudentId);
        assertNotNull(student, "El estudiante no debería ser nulo");

        student.setFirstName("Pedro");
        student.setEmail("pedro.perez@example.com");
        studentRepository.save(student);

        Student updatedStudent = studentRepository.get(savedStudentId);
        assertNotNull(updatedStudent, "El estudiante actualizado no debería ser nulo");
        assertEquals("Pedro", updatedStudent.getFirstName(), "El nombre no se actualizó correctamente");
        assertEquals("pedro.perez@example.com", updatedStudent.getEmail(), "El email no se actualizó correctamente");
    }

    @Test
    @Order(4)
    public void testGetAll() {
        Set<Student> students = studentRepository.getAll();

        assertNotNull(students, "La lista de estudiantes no debería ser nula");
        assertFalse(students.isEmpty(), "La lista de estudiantes no debería estar vacía");
    }

    @Test
    @Order(5)
    public void testFindByName() {
        List<Student> students = studentRepository.findByName("Pedro");

        assertNotNull(students, "La lista de estudiantes no debería ser nula");
        assertFalse(students.isEmpty(), "La lista de estudiantes no debería estar vacía");
        assertTrue(students.get(0).getFirstName().contains("Pedro"), "El nombre no coincide");
    }

    @Test
    @Order(6)
    public void testFindByEmail() {
        List<Student> students = studentRepository.findByEmail("pedro.perez@example.com");

        assertNotNull(students, "La lista de estudiantes no debería ser nula");
        assertFalse(students.isEmpty(), "La lista de estudiantes no debería estar vacía");
        assertEquals("pedro.perez@example.com", students.get(0).getEmail(), "El email no coincide");
    }

    @Test
    @Order(7)
    public void testFindByPhone() {
        List<Student> students = studentRepository.findByPhone("123456789");

        assertNotNull(students, "La lista de estudiantes no debería ser nula");
        assertFalse(students.isEmpty(), "La lista de estudiantes no debería estar vacía");
        assertEquals("123456789", students.get(0).getPhoneNumber(), "El teléfono no coincide");
    }
}
