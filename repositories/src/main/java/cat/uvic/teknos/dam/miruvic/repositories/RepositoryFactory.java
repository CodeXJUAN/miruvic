package cat.uvic.teknos.dam.miruvic.repositories;

public interface RepositoryFactory {
    AddressRepository<Address> getAddressRepository();
    StudentRepository<Student> getStudentRepository();
    PaymentRepository<Payment> getPaymentRepository();
    ReservationRepository<Reservation> getReservationRepository();
    RoomRepository<Room> getRoomRepository();
    ServiceRepository getServiceRepository();
}
