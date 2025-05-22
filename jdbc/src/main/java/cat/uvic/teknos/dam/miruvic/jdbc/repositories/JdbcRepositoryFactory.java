package cat.uvic.teknos.dam.miruvic.jdbc.repositories;

import cat.uvic.teknos.dam.miruvic.model.Room;
import cat.uvic.teknos.dam.miruvic.model.Student;
import cat.uvic.teknos.dam.miruvic.model.Service;
import cat.uvic.teknos.dam.miruvic.model.Address;
import cat.uvic.teknos.dam.miruvic.model.Payment;
import cat.uvic.teknos.dam.miruvic.model.Reservation;
import cat.uvic.teknos.dam.miruvic.repositories.RoomRepository;
import cat.uvic.teknos.dam.miruvic.repositories.StudentRepository;
import cat.uvic.teknos.dam.miruvic.repositories.ServiceRepository;
import cat.uvic.teknos.dam.miruvic.repositories.AddressRepository;
import cat.uvic.teknos.dam.miruvic.repositories.PaymentRepository;
import cat.uvic.teknos.dam.miruvic.repositories.ReservationRepository;
import cat.uvic.teknos.dam.miruvic.jdbc.exceptions.DataSourceException;
import cat.uvic.teknos.dam.miruvic.jdbc.datasources.DataSource;
import cat.uvic.teknos.dam.miruvic.jdbc.datasources.SingleConnectionDataSource;

public class JdbcRepositoryFactory {

    private final DataSource dataSource;

    public JdbcRepositoryFactory() {
        dataSource = new SingleConnectionDataSource();
    }
    
    @Override
    public static RoomRepository<Room> getRoomRepository() {
        return new JdbcRoomRepository(dataSource);
    }
    
    @Override
    public static StudentRepository<Student> getStudentRepository() {
        return new JdbcStudentRepository(dataSource);
    }
    
    @Override
    public static ServiceRepository getServiceRepository() {
        return new JdbcServiceRepository(dataSource);
    }
    
    @Override
    public static AddressRepository<Address> getAddressRepository() {
        return new JdbcAddressRepository(dataSource);
    }
    
    @Override
    public static PaymentRepository<Payment> getPaymentRepository() {
        return new JdbcPaymentRepository(dataSource);
    }
    
    @Override
    public static ReservationRepository<Reservation> ReservationRepository() {
        return new JdbcReservationRepository(dataSource);
    }
}
