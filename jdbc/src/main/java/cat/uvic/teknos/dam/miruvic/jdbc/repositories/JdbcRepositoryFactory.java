package cat.uvic.teknos.dam.miruvic.jdbc.repositories;

import cat.uvic.teknos.dam.miruvic.repositories.*;
import cat.uvic.teknos.dam.miruvic.jdbc.datasources.DataSource;
import cat.uvic.teknos.dam.miruvic.jdbc.datasources.SingleConnectionDataSource;

public class JdbcRepositoryFactory implements RepositoryFactory{

    private final DataSource dataSource;

    public JdbcRepositoryFactory() {
        this.dataSource = new SingleConnectionDataSource();
    }
    
    @Override
    public RoomRepository getRoomRepository() {
        return new JdbcRoomRepository(dataSource);
    }
    
    @Override
    public StudentRepository getStudentRepository() {
        return new JdbcStudentRepository(dataSource);
    }
    
    @Override
    public ServiceRepository getServiceRepository() {
        return new JdbcServiceRepository(dataSource);
    }
    
    @Override
    public AddressRepository getAddressRepository() {
        return new JdbcAddressRepository(dataSource);
    }
    
    @Override
    public PaymentRepository getPaymentRepository() {
        return new JdbcPaymentRepository(dataSource);
    }
    
    @Override
    public ReservationRepository getReservationRepository() {
        return new JdbcReservationRepository(dataSource);
    }

    @Override
    public ReservationServiceRepository getReservationServiceRepository() {
        return new JdbcReservationServiceRepository(dataSource);
    }
}
