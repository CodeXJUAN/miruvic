package cat.uvic.teknos.dam.miruvic.jpa.tests;

import cat.uvic.teknos.dam.miruvic.jpa.model.*;
import cat.uvic.teknos.dam.miruvic.jpa.repository.*;
import cat.uvic.teknos.dam.miruvic.model.Reservation;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class JpaReservationIT {

    private static EntityManager entityManager;
    private static EntityManagerFactory entityManagerFactory;
    private static JpaReservationRepository reservationRepository;
    private static Integer savedReservationId;
    private static Integer savedStudentId;
    private static Integer savedRoomId;
    private static Integer savedAddressId;

    @BeforeAll
    public static void setup() {
        entityManagerFactory = Persistence.createEntityManagerFactory("miruvic_test");
        entityManager = entityManagerFactory.createEntityManager();
        reservationRepository = new JpaReservationRepository(entityManagerFactory);
    }

    @AfterAll
    public static void cleanup() {
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.getTransaction().begin();
            // Elimina la reserva primero
            if (savedReservationId != null) {
                JpaReservation reservation = entityManager.find(JpaReservation.class, savedReservationId);
                if (reservation != null) {
                    entityManager.remove(reservation);
                }
            }
            // Elimina el estudiante
            if (savedStudentId != null) {
                JpaStudent student = entityManager.find(JpaStudent.class, savedStudentId);
                if (student != null) {
                    entityManager.remove(student);
                }
            }
            // Elimina la habitación
            if (savedRoomId != null) {
                JpaRoom room = entityManager.find(JpaRoom.class, savedRoomId);
                if (room != null) {
                    entityManager.remove(room);
                }
            }
            // Elimina la dirección
            if (savedAddressId != null) {
                JpaAddress address = entityManager.find(JpaAddress.class, savedAddressId);
                if (address != null) {
                    entityManager.remove(address);
                }
            }
            entityManager.getTransaction().commit();
        }
        if (entityManager != null) {
            entityManager.close();
        }
        if (entityManagerFactory != null) {
            entityManagerFactory.close();
        }
    }

    @Test
    @Order(1)
    public void testSaveReservation() {
        entityManager.getTransaction().begin();

        // 1. Crear y persistir la dirección
        JpaAddress address = new JpaAddress();
        address.setStreet("Calle Reserva");
        address.setCity("Madrid");
        address.setState("Madrid");
        address.setZipCode("28001");
        address.setCountry("España");
        entityManager.persist(address);

        // 2. Crear y persistir la habitación
        JpaRoom room = new JpaRoom();
        room.setRoomNumber("301");
        room.setFloor(3);
        room.setCapacity(1);
        room.setType("Individual");
        room.setPrice(new BigDecimal("60.00"));
        entityManager.persist(room);

        // 3. Crear y persistir el estudiante
        JpaStudent student = new JpaStudent();
        student.setFirstName("María");
        student.setLastName("López");
        student.setEmail("maria@example.com");
        student.setPasswordHash("hash123");
        student.setPhoneNumber("987654321");
        student.setAddress(address);
        entityManager.persist(student);

        // 4. Crear y persistir la reserva
        JpaReservation reservation = new JpaReservation();
        reservation.setStartDate(LocalDate.now());
        reservation.setEndDate(LocalDate.now().plusDays(3));
        reservation.setStatus("ACTIVA");
        reservation.setStudent(student);
        reservation.setRoom(room);
        entityManager.persist(reservation);

        entityManager.merge(student);

        entityManager.getTransaction().commit();

        savedAddressId = address.getId();
        savedRoomId = room.getId();
        savedStudentId = student.getId();
        savedReservationId = reservation.getId();

        assertNotNull(savedAddressId, "El ID de la dirección no debería ser nulo");
        assertNotNull(savedRoomId, "El ID de la habitación no debería ser nulo");
        assertNotNull(savedStudentId, "El ID del estudiante no debería ser nulo");
        assertNotNull(savedReservationId, "El ID de la reserva no debería ser nulo");
    }

    @Test
    @Order(2)
    public void testGetReservation() {
        Reservation reservation = reservationRepository.get(savedReservationId);

        assertNotNull(reservation, "La reserva no debería ser nula");
        assertEquals("ACTIVA", reservation.getStatus(), "El estado de la reserva no coincide");
        assertNotNull(reservation.getStudent(), "El estudiante asociado no debería ser nulo");
        assertNotNull(reservation.getRoom(), "La habitación asociada no debería ser nula");
    }

    @Test
    @Order(3)
    public void testUpdateReservation() {
        Reservation reservation = reservationRepository.get(savedReservationId);
        assertNotNull(reservation, "La reserva no debería ser nula");

        LocalDate newEndDate = LocalDate.now().plusDays(5);
        reservation.setEndDate(newEndDate);
        reservation.setStatus("MODIFICADA");
        reservationRepository.save(reservation);

        Reservation updatedReservation = reservationRepository.get(savedReservationId);
        assertNotNull(updatedReservation, "La reserva actualizada no debería ser nula");
        assertEquals("MODIFICADA", updatedReservation.getStatus(), "El estado de la reserva no se actualizó correctamente");
        assertEquals(newEndDate, updatedReservation.getEndDate(), "La fecha de fin no se actualizó correctamente");
    }

    @Test
    @Order(4)
    public void testGetAll() {
        Set<Reservation> reservations = reservationRepository.getAll();
        assertNotNull(reservations, "La lista de reservas no debería ser nula");
        assertFalse(reservations.isEmpty(), "La lista de reservas no debería estar vacía");
    }

    @Test
    @Order(5)
    public void testFindByDate() {
        List<Reservation> reservations = reservationRepository.findByDate(LocalDate.now());
        assertNotNull(reservations, "La lista de reservas no debería ser nula");
        assertFalse(reservations.isEmpty(), "La lista de reservas no debería estar vacía");
    }
}