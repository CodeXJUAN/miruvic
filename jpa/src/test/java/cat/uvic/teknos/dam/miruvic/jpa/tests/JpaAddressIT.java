package cat.uvic.teknos.dam.miruvic.jpa.tests;

import cat.uvic.teknos.dam.miruvic.jpa.model.JpaStudent;
import jakarta.persistence.EntityManager;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import cat.uvic.teknos.dam.miruvic.jpa.model.JpaAddress;
import cat.uvic.teknos.dam.miruvic.jpa.repository.JpaAddressRepository;
import cat.uvic.teknos.dam.miruvic.model.Address;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.math.BigDecimal;
import org.junit.jupiter.api.*;
import cat.uvic.teknos.dam.miruvic.jpa.model.JpaRoom;
import java.time.LocalDate;
import cat.uvic.teknos.dam.miruvic.jpa.model.JpaReservation;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class JpaAddressIT {
    private static EntityManager entityManager;
    private static EntityManagerFactory entityManagerFactory;
    private static JpaAddressRepository addressRepository;
    private static Integer savedAddressId;

    @BeforeAll
    public static void setup() {
        entityManagerFactory = Persistence.createEntityManagerFactory("miruvic_test");
        entityManager = entityManagerFactory.createEntityManager();
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
    public void testSaveAddress() {
        entityManager.getTransaction().begin();

        // 1. Crear y persistir la dirección
        JpaAddress address = new JpaAddress();
        address.setStreet("Calle Test");
        address.setCity("Barcelona");
        address.setState("Cataluña");
        address.setZipCode("08001");
        address.setCountry("España");
        entityManager.persist(address);

        // 2. Crear y persistir la habitación
        JpaRoom room = new JpaRoom();
        room.setRoomNumber("101");
        room.setFloor(1);
        room.setCapacity(2);
        room.setType("SINGLE");
        room.setPrice(new BigDecimal("100.00"));
        entityManager.persist(room);

        // 3. Crear y persistir el estudiante (sin reserva aún)
        JpaStudent student = new JpaStudent();
        student.setFirstName("Juan");
        student.setLastName("Pérez");
        student.setEmail("juan.perez@example.com");
        student.setPasswordHash("hashedPassword123");
        student.setPhoneNumber("123456789");
        student.setAddress(address);
        entityManager.persist(student);

        // 4. Crear la reserva, asignar estudiante y habitación, y persistirla
        JpaReservation reservation = new JpaReservation();
        reservation.setStartDate(LocalDate.now());
        reservation.setEndDate(LocalDate.now().plusDays(7));
        reservation.setStatus("ACTIVE");
        reservation.setRoom(room);
        reservation.setStudent(student);
        entityManager.persist(reservation);

        // 5. Asignar la reserva al estudiante y mergear (si la relación es bidireccional)
        student.setReservation(reservation);
        entityManager.merge(student);

        entityManager.getTransaction().commit();

        assertNotNull(address.getId(), "El ID de la dirección no debería ser nulo después de guardar");
        savedAddressId = address.getId();
    }

    @Test
    @Order(2)
    public void testGetAddress() {
        Address address = addressRepository.get(savedAddressId);
        assertNotNull(address, "La dirección no debería ser nula");
        assertEquals("Calle Test", address.getStreet(), "La calle no coincide");
        assertEquals("Barcelona", address.getCity(), "La ciudad no coincide");
        assertEquals("Cataluña", address.getState(), "El estado no coincide");
        assertEquals("08001", address.getZipCode(), "El código postal no coincide");
        assertEquals("España", address.getCountry(), "El país no coincide");
    }

    @Test
    @Order(3)
    public void testUpdateAddress() {
        Address address = addressRepository.get(savedAddressId);
        assertNotNull(address, "La dirección no debería ser nula");
        address.setStreet("Calle Modificada");
        address.setCity("Madrid");
        addressRepository.save(address);
        Address updatedAddress = addressRepository.get(savedAddressId);
        assertNotNull(updatedAddress, "La dirección actualizada no debería ser nula");
        assertEquals("Calle Modificada", updatedAddress.getStreet(), "La calle no se actualizó correctamente");
        assertEquals("Madrid", updatedAddress.getCity(), "La ciudad no se actualizó correctamente");
    }

    @Test
    @Order(4)
    public void testGetAll() {
        Set<Address> addresses = addressRepository.getAll();
        assertNotNull(addresses, "La lista de direcciones no debería ser nula");
        assertFalse(addresses.isEmpty(), "La lista de direcciones no debería estar vacía");
    }

    @Test
    @Order(5)
    public void testFindByCity() {
        List<Address> addresses = addressRepository.findByCity("Madrid");
        assertNotNull(addresses, "La lista de direcciones no debería ser nula");
        assertFalse(addresses.isEmpty(), "La lista de direcciones no debería estar vacía");
        assertEquals("Madrid", addresses.get(0).getCity(), "La ciudad no coincide");
    }

    @Test
    @Order(6)
    public void testFindByPostalCode() {
        List<Address> addresses = addressRepository.findByPostalCode("08001");
        assertNotNull(addresses, "La lista de direcciones no debería ser nula");
    }

    @Test
    @Order(7)
    public void testFindByCountry() {
        List<Address> addresses = addressRepository.findByCountry("España");
        assertNotNull(addresses, "La lista de direcciones no debería ser nula");
        assertFalse(addresses.isEmpty(), "La lista de direcciones no debería estar vacía");
        assertEquals("España", addresses.get(0).getCountry(), "El país no coincide");
    }

    @Test
    @Order(8)
    public void testDeleteAddress() {
        entityManager.getTransaction().begin();

        // Buscar estudiantes que usan la dirección
        List<JpaStudent> students = entityManager.createQuery(
                        "SELECT s FROM JpaStudent s WHERE s.address.id = :addressId", JpaStudent.class)
                .setParameter("addressId", savedAddressId)
                .getResultList();

        for (JpaStudent student : students) {
            JpaReservation reservation = (JpaReservation) student.getReservation();
            if (reservation != null) {
                entityManager.remove(reservation); // Elimina primero la reserva
            }
            entityManager.remove(student); // Luego el estudiante
        }

        entityManager.getTransaction().commit();

        // Ahora sí, elimina la dirección
        Address address = addressRepository.get(savedAddressId);
        assertNotNull(address, "La dirección no debería ser nula");
        addressRepository.delete(address);
        Address deletedAddress = addressRepository.get(savedAddressId);
        assertNull(deletedAddress, "La dirección debería ser nula después de eliminarla");
    }
}