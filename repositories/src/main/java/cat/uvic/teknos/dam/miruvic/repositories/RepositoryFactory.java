package cat.uvic.teknos.dam.miruvic.repositories;

public interface RepositoryFactory {

    AddressRepository getAddressRepository();

    StudentRepository getStudentRepository();

    PaymentRepository getPaymentRepository();

    ReservationRepository getReservationRepository();

    RoomRepository getRoomRepository();

    ServiceRepository getServiceRepository();
}
