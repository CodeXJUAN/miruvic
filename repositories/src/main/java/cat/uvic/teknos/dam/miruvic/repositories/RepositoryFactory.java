package cat.uvic.teknos.dam.miruvic.repositories;


public interface RepositoryFactory {

    AddressRepository getAddressRepository();

    StudentRepository getStudentRepository();

    PaymentRepository getPaymentRepository();

    ReservationRepository getReservationRepository();

    ReservationServiceRepository getReservationServiceRepository();

    RoomRepository getRoomRepository();

    ServiceRepository getServiceRepository();
}
