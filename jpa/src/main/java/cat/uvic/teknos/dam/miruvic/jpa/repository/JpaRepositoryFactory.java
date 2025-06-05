package cat.uvic.teknos.dam.miruvic.jpa.repository;

import cat.uvic.teknos.dam.miruvic.repositories.*;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JpaRepositoryFactory implements RepositoryFactory {

    private final EntityManagerFactory entityManagerFactory;

    public JpaRepositoryFactory() {
        entityManagerFactory = Persistence.createEntityManagerFactory("RUVIC");
    }

    public AddressRepository getAddressRepository() {
        return new JpaAddressRepository(entityManagerFactory);
    }

    public StudentRepository getStudentRepository() {
        return new JpaStudentRepository(entityManagerFactory);
    }

    public RoomRepository getRoomRepository() {
        return new JpaRoomRepository(entityManagerFactory);
    }

    public ReservationRepository getReservationRepository() {
        return new JpaReservationRepository(entityManagerFactory);
    }

    public PaymentRepository getPaymentRepository() {
        return new JpaPaymentRepository(entityManagerFactory);
    }

    public ServiceRepository getServiceRepository() {
        return new JpaServiceRepository(entityManagerFactory);
    }

    public ReservationServiceRepository getReservationServiceRepository() {
        return new JpaReservationServiceRepository(entityManagerFactory);
    }
}
