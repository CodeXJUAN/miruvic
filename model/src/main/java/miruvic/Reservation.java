package miruvic;

import java.time.LocalDate;
import java.util.Set;

public interface Reservation {
    // Métodos de la interfaz
    int getId();

    void setId(int id);

    Student getStudent();

    void setStudent(Student student);

    Room getRoom();

    void setRoom(Room room);

    LocalDate getStartDate();

    void setStartDate(LocalDate startDate);

    LocalDate getEndDate();

    void setEndDate(LocalDate endDate);

    ReservationStatus getStatus();

    void setStatus(ReservationStatus status);

    Set<Service> getServices();

    void setServices(Set<Service> services);

    // Enum para los estados de reserva
    enum ReservationStatus {
        Pending, Confirmed, Cancelled
    }
}