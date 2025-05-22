package cat.uvic.teknos.dam.miruvic.repositories;

import cat.uvic.teknos.dam.miruvic.model.Address;
import cat.uvic.teknos.dam.miruvic.model.Student;
import cat.uvic.teknos.dam.miruvic.model.Payment;
import cat.uvic.teknos.dam.miruvic.model.Reservation;
import cat.uvic.teknos.dam.miruvic.model.Room;
import cat.uvic.teknos.dam.miruvic.model.Service;

public interface RepositoryFactory {
    AddressRepository<Address> getAddressRepository();
    StudentRepository<Student> getStudentRepository();
    PaymentRepository<Payment> getPaymentRepository();
    ReservationRepository<Reservation> getReservationRepository();
    RoomRepository<Room> getRoomRepository();
    ServiceRepository<Service> getServiceRepository();
}
