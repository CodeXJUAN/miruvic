package cat.uvic.teknos.dam.miruvic.model;

public interface ModelFactory {

    Address newAddress();

    Student newStudent();

    Payment newPayment();

    Reservation newReservation();

    Room newRoom();

    ReservationService newReservationService();

    Service newService();
}
