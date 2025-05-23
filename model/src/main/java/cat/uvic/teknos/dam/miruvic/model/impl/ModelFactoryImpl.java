package cat.uvic.teknos.dam.miruvic.model.impl;

import cat.uvic.teknos.dam.miruvic.model.Address;
import cat.uvic.teknos.dam.miruvic.model.ModelFactory;
import cat.uvic.teknos.dam.miruvic.model.Payment;
import cat.uvic.teknos.dam.miruvic.model.Reservation;
import cat.uvic.teknos.dam.miruvic.model.Room;
import cat.uvic.teknos.dam.miruvic.model.Service;
import cat.uvic.teknos.dam.miruvic.model.Student;

public class ModelFactoryImpl implements ModelFactory {
    @Override
    public Address newAddres(){
        return new AddressImpl();
    }

    @Override
    public Student newStudent(){
        return new StudentImpl();
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