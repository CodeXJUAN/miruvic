package cat.uvic.teknos.dam.miruvic.jpa.tests;

import cat.uvic.teknos.dam.miruvic.jpa.model.*;
import cat.uvic.teknos.dam.miruvic.jpa.repository.JpaPaymentRepository;
import cat.uvic.teknos.dam.miruvic.model.Payment;
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
public class JpaPaymentIT {

    private static EntityManager entityManager;
    private static EntityManagerFactory entityManagerFactory;
    private static JpaPaymentRepository paymentRepository;
    private static Integer savedPaymentId;

    @BeforeAll
    public static void setup() {
        entityManagerFactory = Persistence.createEntityManagerFactory("miruvic_test");
        entityManager = entityManagerFactory.createEntityManager();
        paymentRepository = new JpaPaymentRepository(entityManagerFactory);
    }

    @AfterAll
    public static void tearDown() {
        if (entityManagerFactory != null) {
            entityManagerFactory.close();
        }
    }

    @Test
    @Order(1)
    public void testSavePayment() {
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

        // 3. Crear y persistir el estudiante
        JpaStudent student = new JpaStudent();
        student.setFirstName("Juan");
        student.setLastName("Pérez");
        student.setEmail("juan.perez@example.com");
        student.setPasswordHash("hashedPassword123");
        student.setPhoneNumber("123456789");
        student.setAddress(address);
        entityManager.persist(student);

        // 4. Crear y persistir la reserva
        JpaReservation reservation = new JpaReservation();
        reservation.setStartDate(LocalDate.now());
        reservation.setEndDate(LocalDate.now().plusDays(7));
        reservation.setStatus("ACTIVE");
        reservation.setRoom(room);
        reservation.setStudent(student);
        entityManager.persist(reservation);

        // 5. Crear y persistir el pago
        JpaPayment payment = new JpaPayment();
        payment.setAmount(new BigDecimal("200.00"));
        payment.setPaymentDate(LocalDate.now());
        payment.setPaymentMethod("Tarjeta de crédito");
        payment.setStatus("COMPLETADO");
        payment.setReservation(reservation);
        entityManager.persist(payment);

        entityManager.merge(address);
        entityManager.merge(room);
        entityManager.merge(student);
        entityManager.merge(reservation);
        entityManager.merge(payment);

        entityManager.getTransaction().commit();
        entityManager.close();

        assertNotNull(payment.getId(), "El ID del pago no debería ser nulo después de guardar");
        savedPaymentId = payment.getId();
    }

    @Test
    @Order(2)
    public void testGetPayment() {
        Payment payment = paymentRepository.get(savedPaymentId);

        assertNotNull(payment, "El pago no debería ser nulo");
        assertEquals(0, new BigDecimal("200.00").compareTo(payment.getAmount()), "El monto del pago no coincide");
        assertEquals("Tarjeta de crédito", payment.getPaymentMethod(), "El método de pago no coincide");
        assertNotNull(payment.getReservation(), "La reserva asociada no debería ser nula");
    }

    @Test
    @Order(3)
    public void testUpdatePayment() {
        Payment payment = paymentRepository.get(savedPaymentId);
        assertNotNull(payment, "El pago no debería ser nulo");

        payment.setAmount(new BigDecimal("250.00"));
        payment.setPaymentMethod("Transferencia bancaria");
        paymentRepository.save(payment);

        Payment updatedPayment = paymentRepository.get(savedPaymentId);
        assertNotNull(updatedPayment, "El pago actualizado no debería ser nulo");
        assertEquals(0, new BigDecimal("250.00").compareTo(updatedPayment.getAmount()),
                "El monto del pago no se actualizó correctamente");
        assertEquals("Transferencia bancaria", updatedPayment.getPaymentMethod(),
                "El método de pago no se actualizó correctamente");
    }

    @Test
    @Order(4)
    public void testGetAll() {
        Set<Payment> payments = paymentRepository.getAll();
        assertNotNull(payments, "La lista de pagos no debería ser nula");
        assertFalse(payments.isEmpty(), "La lista de pagos no debería estar vacía");
    }

    @Test
    @Order(5)
    public void testFindByMethod() {
        List<Payment> payments = paymentRepository.findByMethod("Transferencia bancaria");
        assertNotNull(payments, "La lista de pagos no debería ser nula");
        assertFalse(payments.isEmpty(), "La lista de pagos no debería estar vacía");
        assertEquals("Transferencia bancaria", payments.get(0).getPaymentMethod(),
                "El método de pago no coincide");
    }

    @Test
    @Order(6)
    public void testFindByAmountRange() {
        List<Payment> payments = paymentRepository.findByAmountRange(200.0, 300.0);
        assertNotNull(payments, "La lista de pagos no debería ser nula");
        assertFalse(payments.isEmpty(), "La lista de pagos no debería estar vacía");
        assertTrue(payments.get(0).getAmount().doubleValue() >= 200.0
                        && payments.get(0).getAmount().doubleValue() <= 300.0,
                "El monto del pago no está dentro del rango especificado");
    }
}