package cat.uvic.teknos.dam.miruvic.jpa.tests;

import cat.uvic.teknos.dam.miruvic.jpa.model.JpaService;
import cat.uvic.teknos.dam.miruvic.jpa.repository.JpaServiceRepository;
import cat.uvic.teknos.dam.miruvic.model.Service;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class JpaServiceIT {

    private static EntityManagerFactory entityManagerFactory;
    private static JpaServiceRepository serviceRepository;
    private static Integer savedServiceId;

    @BeforeAll
    public static void setup() {
        entityManagerFactory = Persistence.createEntityManagerFactory("miruvic_test");
        serviceRepository = new JpaServiceRepository(entityManagerFactory);
    }

    @AfterAll
    public static void tearDown() {
        if (entityManagerFactory != null) {
            entityManagerFactory.close();
        }
    }

    @Test
    @Order(1)
    public void testSaveService() {
        // Crear un nuevo servicio
        JpaService service = new JpaService();
        service.setServiceName("Limpieza");
        service.setDescription("Servicio de limpieza diaria");
        service.setPrice(new BigDecimal("25.00"));

        // Guardar el servicio
        serviceRepository.save(service);

        // Verificar que se ha asignado un ID
        assertNotNull(service.getId(), "El ID del servicio no debería ser nulo después de guardar");
        savedServiceId = service.getId();
    }

    @Test
    @Order(2)
    public void testGetService() {
        // Obtener el servicio guardado
        Service service = serviceRepository.get(savedServiceId);

        // Verificar que el servicio existe y tiene los datos correctos
        assertNotNull(service, "El servicio no debería ser nulo");
        assertEquals("Limpieza", service.getServiceName(), "El nombre del servicio no coincide");
        assertEquals("Servicio de limpieza diaria", service.getDescription(), "La descripción no coincide");
        assertEquals(0, new BigDecimal("25.00").compareTo(service.getPrice()), "El precio no coincide");
    }

    @Test
    @Order(3)
    public void testUpdateService() {
        // Obtener el servicio guardado
        Service service = serviceRepository.get(savedServiceId);
        assertNotNull(service, "El servicio no debería ser nulo");

        // Modificar el servicio
        service.setServiceName("Limpieza Premium");
        service.setDescription("Servicio de limpieza premium diaria");
        service.setPrice(new BigDecimal("35.00"));
        serviceRepository.save(service);

        // Obtener el servicio actualizado
        Service updatedService = serviceRepository.get(savedServiceId);
        assertNotNull(updatedService, "El servicio actualizado no debería ser nulo");
        assertEquals("Limpieza Premium", updatedService.getServiceName(), "El nombre del servicio no se actualizó correctamente");
        assertEquals("Servicio de limpieza premium diaria", updatedService.getDescription(), "La descripción no se actualizó correctamente");
        assertEquals(0, new BigDecimal("35.00").compareTo(updatedService.getPrice()), "El precio no se actualizó correctamente");
    }

    @Test
    @Order(4)
    public void testGetAll() {
        // Obtener todos los servicios
        Set<Service> services = serviceRepository.getAll();

        // Verificar que la lista no es nula y contiene al menos un servicio
        assertNotNull(services, "La lista de servicios no debería ser nula");
        assertFalse(services.isEmpty(), "La lista de servicios no debería estar vacía");
    }

    @Test
    @Order(5)
    public void testFindByName() {
        // Buscar servicios por nombre
        List<Service> services = serviceRepository.findByName("Limpieza Premium");

        // Verificar que la lista no es nula y contiene al menos un servicio
        assertNotNull(services, "La lista de servicios no debería ser nula");
        assertFalse(services.isEmpty(), "La lista de servicios no debería estar vacía");
        assertEquals("Limpieza Premium", services.get(0).getServiceName(), "El nombre del servicio no coincide");
    }

    @Test
    @Order(6)
    public void testFindByType() {
        // Buscar servicios por tipo (usando descripción)
        List<Service> services = serviceRepository.findByType("premium");

        // Verificar que la lista no es nula y contiene al menos un servicio
        assertNotNull(services, "La lista de servicios no debería ser nula");
        assertFalse(services.isEmpty(), "La lista de servicios no debería estar vacía");
        assertTrue(services.get(0).getDescription().toLowerCase().contains("premium"),
                "La descripción del servicio debería contener la palabra 'premium'");
    }
}