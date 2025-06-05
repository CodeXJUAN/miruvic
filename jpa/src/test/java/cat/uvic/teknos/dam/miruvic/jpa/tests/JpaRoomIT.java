package cat.uvic.teknos.dam.miruvic.jpa.tests;

import cat.uvic.teknos.dam.miruvic.jpa.model.JpaRoom;
import cat.uvic.teknos.dam.miruvic.jpa.repository.JpaRoomRepository;
import cat.uvic.teknos.dam.miruvic.model.Room;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class JpaRoomIT {

    private static EntityManager entityManager;
    private static EntityManagerFactory entityManagerFactory;
    private static JpaRoomRepository roomRepository;
    private static Integer savedRoomId;

    @BeforeAll
    public static void setup() {
        entityManagerFactory = Persistence.createEntityManagerFactory("miruvic_test");
        entityManager = entityManagerFactory.createEntityManager();
        roomRepository = new JpaRoomRepository(entityManagerFactory);
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
    public void testSaveRoom() {
        entityManager.getTransaction().begin();

        JpaRoom room = new JpaRoom();
        room.setRoomNumber("101");
        room.setFloor(1);
        room.setCapacity(2);
        room.setType("Individual");
        room.setPrice(new BigDecimal("50.00"));
        entityManager.persist(room);

        entityManager.getTransaction().commit();

        assertNotNull(room.getId(), "El ID de la habitación no debería ser nulo después de guardar");
        savedRoomId = room.getId();
    }

    @Test
    @Order(2)
    public void testGetRoom() {
        Room room = roomRepository.get(savedRoomId);

        assertNotNull(room, "La habitación no debería ser nula");
        assertEquals("101", room.getRoomNumber(), "El número de habitación no coincide");
        assertEquals(1, room.getFloor(), "El piso no coincide");
        assertEquals(2, room.getCapacity(), "La capacidad no coincide");
        assertEquals("Individual", room.getType(), "El tipo no coincide");
        assertEquals(0, new BigDecimal("50.00").compareTo(room.getPrice()), "El precio no coincide");
    }

    @Test
    @Order(3)
    public void testUpdateRoom() {
        Room room = roomRepository.get(savedRoomId);
        assertNotNull(room, "La habitación no debería ser nula");

        room.setRoomNumber("102");
        room.setCapacity(3);
        room.setPrice(new BigDecimal("75.00"));
        roomRepository.save(room);

        Room updatedRoom = roomRepository.get(savedRoomId);
        assertNotNull(updatedRoom, "La habitación actualizada no debería ser nula");
        assertEquals("102", updatedRoom.getRoomNumber(), "El número de habitación no se actualizó correctamente");
        assertEquals(3, updatedRoom.getCapacity(), "La capacidad no se actualizó correctamente");
        assertEquals(0, new BigDecimal("75.00").compareTo(updatedRoom.getPrice()), "El precio no se actualizó correctamente");
    }

    @Test
    @Order(4)
    public void testGetAll() {
        Set<Room> rooms = roomRepository.getAll();
        assertNotNull(rooms, "La lista de habitaciones no debería ser nula");
        assertFalse(rooms.isEmpty(), "La lista de habitaciones no debería estar vacía");
    }

    @Test
    @Order(5)
    public void testFindByNumber() {
        List<Room> rooms = roomRepository.findByNumber("102");
        assertNotNull(rooms, "La lista de habitaciones no debería ser nula");
        assertFalse(rooms.isEmpty(), "La lista de habitaciones no debería estar vacía");
        assertEquals("102", rooms.get(0).getRoomNumber(), "El número de habitación no coincide");
    }

    @Test
    @Order(6)
    public void testFindByType() {
        List<Room> rooms = roomRepository.findByType("Individual");
        assertNotNull(rooms, "La lista de habitaciones no debería ser nula");
        assertFalse(rooms.isEmpty(), "La lista de habitaciones no debería estar vacía");
        assertEquals("Individual", rooms.get(0).getType(), "El tipo de habitación no coincide");
    }
}
