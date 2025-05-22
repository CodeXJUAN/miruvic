package cat.uvic.teknos.dam.miruvic.model;

public interface ModelFactory {
    Address newAddres();
    Student newStudent();
    Payment newPayment();
    Reservation newReservation();
    Room newRoom();
    Service newService();
}
