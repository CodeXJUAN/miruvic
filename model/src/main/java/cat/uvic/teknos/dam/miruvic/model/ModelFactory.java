package cat.uvic.teknos.dam.miruvic.model;

import cat.uvic.teknos.dam.miruvic.model.impl.AddressImpl;
import cat.uvic.teknos.dam.miruvic.model.impl.PaymentImpl;
import cat.uvic.teknos.dam.miruvic.model.impl.ReservationImpl;
import cat.uvic.teknos.dam.miruvic.model.impl.RoomImpl;
import cat.uvic.teknos.dam.miruvic.model.impl.ServiceImpl;
import cat.uvic.teknos.dam.miruvic.model.impl.StudentImpl;

public class ModelFactory {

    public static Address createAddress() {
        return new AddressImpl();
    }

    public static Payment createPayment() {
        return new PaymentImpl();
    }

    public static Reservation createReservation() {
        return new ReservationImpl();
    }

    public static Room createRoom() {
        return new RoomImpl();
    }

    public static Service createService() {
        return new ServiceImpl();
    }

    public static Student createStudent() {
        return new StudentImpl();
    }
}
