package cat.uvic.teknos.dam.miruvic.model.impl;

import cat.uvic.teknos.dam.miruvic.model.*;

public class ModelFactoryImpl implements ModelFactory {
    @Override
    public Address newAddress(){
        return new Address();
    }

    @Override
    public Student newStudent(){
        return new Student();
    }

    @Override
    public Payment newPayment(){
        return new PaymentImpl();
    }

    @Override
    public Reservation newReservation(){
        return new ReservationImpl();
    }

    @Override
    public Room newRoom(){
        return new RoomImpl();
    }

    @Override
    public Service newService(){
        return new ServiceImpl();
    }
}
