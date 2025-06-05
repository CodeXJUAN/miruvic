package cat.uvic.teknos.dam.miruvic.jpa.tests;

import cat.uvic.teknos.dam.miruvic.jpa.model.*;
import cat.uvic.teknos.dam.miruvic.jpa.repository.*;
import cat.uvic.teknos.dam.miruvic.model.ReservationService;
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
public class JpaReservationServiceIT {

    private static EntityManager entityManager;
    private static EntityManagerFactory entityManagerFactory;
    private static JpaReservationServiceRepository reservationServiceRepository;
    private static JpaReservationRepository reservationRepository;
    private static JpaServiceRepository serviceRepository;
    private static Integer savedReservationId;
    private static Integer savedServiceId;
    private static ReservationServiceId savedReservationServiceId;

    @BeforeAll
    public static void setup() {
        entityManagerFactory = Persistence.createEntityManagerFactory("miruvic_test");
        entityManager = entityManagerFactory.createEntityManager();
        reservationServiceRepository = new JpaReservationServiceRepository(entityManagerFactory);
        reservationRepository = new JpaReservationRepository(entityManagerFactory);
        serviceRepository = new JpaServiceRepository(entityManagerFactory);
    }

    @AfterAll
    public static void tearDown() {
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.getTransaction().begin();
            // Elimina ReservationService
            if (savedReservationServiceId != null) {
                JpaReservationService rs = entityManager.find(JpaReservationService.class, savedReservationServiceId);
                if (rs != null) {
                    entityManager.remove(rs);
                }
            }
            // Elimina Reservation
            if (savedReservationId != null) {
                JpaReservation reservation = entityManager.find(JpaReservation.class, savedReservationId);
                if (reservation != null) {
                    entityManager.remove(reservation);
                }
            }
            // Elimina Service
            if (savedServiceId != null) {
                JpaService service = entityManager.find(JpaService.class, savedServiceId);
                if (service != null) {
                    entityManager.remove(service);
                }
            }
            // Elimina Address, Room y Student asociados a la reserva
            JpaReservation reservation = entityManager.find(JpaReservation.class, savedReservationId);
            if (reservation != null) {
                JpaStudent student = (JpaStudent) reservation.getStudent();
                if (student != null) {
                    entityManager.remove(entityManager.contains(student) ? student : entityManager.merge(student));
                }
                JpaRoom room = (JpaRoom) reservation.getRoom();
                if (room != null) {
                    entityManager.remove(entityManager.contains(room) ? room : entityManager.merge(room));
                }
                JpaAddress address = student != null ? (JpaAddress) student.getAddress() : null;
                if (address != null) {
                    entityManager.remove(entityManager.contains(address) ? address : entityManager.merge(address));
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
    public void testSetupPrerequisites() {
        entityManager.getTransaction().begin();

        // Crear y persistir la dirección
        JpaAddress address = new JpaAddress();
        address.setStreet("Calle Servicio");
        address.setCity("Valencia");
        address.setState("Valencia");
        address.setZipCode("46001");
        address.setCountry("España");
        entityManager.persist(address);

        // Crear y persistir la habitación
        JpaRoom room = new JpaRoom();
        room.setRoomNumber("401");
        room.setFloor(4);
        room.setCapacity(2);
        room.setType("Doble");
        room.setPrice(new BigDecimal("90.00"));
        entityManager.persist(room);

        // Crear y persistir el estudiante
        JpaStudent student = new JpaStudent();
        student.setFirstName("Carlos");
        student.setLastName("Rodríguez");
        student.setEmail("carlos@example.com");
        student.setPasswordHash("hash456");
        student.setPhoneNumber("555123456");
        student.setAddress(address);
        entityManager.persist(student);

        // Crear y persistir la reserva
        JpaReservation reservation = new JpaReservation();
        reservation.setStartDate(LocalDate.now());
        reservation.setEndDate(LocalDate.now().plusDays(4));
        reservation.setStatus("ACTIVA");
        reservation.setStudent(student);
        reservation.setRoom(room);
        entityManager.persist(reservation);

        entityManager.merge(student);

        // Crear y persistir el servicio
        JpaService service = new JpaService();
        service.setServiceName("Desayuno");
        service.setDescription("Desayuno continental");
        service.setPrice(new BigDecimal("15.00"));
        entityManager.persist(service);

        entityManager.getTransaction().commit();

        savedReservationId = reservation.getId();
        savedServiceId = service.getId();

        assertNotNull(savedReservationId, "El ID de la reserva no debería ser nulo");
        assertNotNull(savedServiceId, "El ID del servicio no debería ser nulo");
    }

    @Test
    @Order(2)
    public void testSaveReservationService() {
        JpaReservation reservation = (JpaReservation) reservationRepository.get(savedReservationId);
        JpaService service = (JpaService) serviceRepository.get(savedServiceId);
        assertNotNull(reservation, "La reserva no debería ser nula");
        assertNotNull(service, "El servicio no debería ser nulo");

        JpaReservationService reservationService = new JpaReservationService();
        ReservationServiceId id = new ReservationServiceId();
        id.setReservationId(reservation.getId());
        id.setServiceId(service.getId());
        reservationService.setId(id);
        reservationService.setReservation(reservation);
        reservationService.setService(service);
        reservationService.setQuantity(2);

        reservationServiceRepository.save(reservationService);
        savedReservationServiceId = id;
    }

    @Test
    @Order(3)
    public void testGetAll() {
        Set<ReservationService> reservationServices = reservationServiceRepository.getAll();
        assertNotNull(reservationServices, "La lista de ReservationService no debería ser nula");
        assertFalse(reservationServices.isEmpty(), "La lista de ReservationService no debería estar vacía");

        boolean found = false;
        for (ReservationService rs : reservationServices) {
            if (rs.getReservation().getId().equals(savedReservationId) && rs.getService().getId().equals(savedServiceId)) {
                found = true;
                assertEquals(2, rs.getQuantity(), "La cantidad no coincide");
                break;
            }
        }
        assertTrue(found, "No se encontró el ReservationService creado");
    }

    @Test
    @Order(4)
    public void testFindByReservationId() {
        List<ReservationService> reservationServices = reservationServiceRepository.findByReservationId(savedReservationId);
        assertNotNull(reservationServices, "La lista de ReservationService no debería ser nula");
        assertFalse(reservationServices.isEmpty(), "La lista de ReservationService no debería estar vacía");
        assertEquals(savedReservationId,
                reservationServices.get(0).getReservation().getId(),
                "El ID de la reserva asociada no coincide");
    }

    @Test
    @Order(5)
    public void testFindByServiceId() {
        List<ReservationService> reservationServices = reservationServiceRepository.findByServiceId(savedServiceId);
        assertNotNull(reservationServices, "La lista de ReservationService no debería ser nula");
        assertFalse(reservationServices.isEmpty(), "La lista de ReservationService no debería estar vacía");
        assertEquals(savedServiceId,
                reservationServices.get(0).getService().getId(),
                "El ID del servicio asociado no coincide");
    }

    @Test
    @Order(6)
    public void testUpdateReservationService() {
        Set<ReservationService> reservationServices = reservationServiceRepository.getAll();
        JpaReservationService reservationServiceToUpdate = null;
        for (ReservationService rs : reservationServices) {
            if (rs.getReservation().getId().equals(savedReservationId) && rs.getService().getId().equals(savedServiceId)) {
                reservationServiceToUpdate = (JpaReservationService) rs;
                break;
            }
        }

        assertNotNull(reservationServiceToUpdate, "No se encontró el ReservationService para actualizar");
        reservationServiceToUpdate.setQuantity(3);
        reservationServiceRepository.save(reservationServiceToUpdate);

        List<ReservationService> updatedList = reservationServiceRepository.findByReservationId(savedReservationId);
        assertFalse(updatedList.isEmpty(), "La lista actualizada no debería estar vacía");
        assertEquals(3, updatedList.get(0).getQuantity(), "La cantidad no se actualizó correctamente");
    }
}