package cat.uvic.teknos.dam.ruvic;

import java.time.LocalDate; // Cambiado de Date a LocalDate para date SQL
import java.util.Set;

public interface Reservation {
    private int id;
    private Student student;
    private Room room;
    private LocalDate startDate; // Cambiado de Date a LocalDate
    private LocalDate endDate; // Cambiado de Date a LocalDate
    private ReservationStatus status; // Enum para Pending, Confirmed, Cancelled
    private Set<Service> services;

    // Enum para los estados de reserva
    public enum ReservationStatus {
        Pending, Confirmed, Cancelled
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public Set<Service> getServices() {
        return services;
    }

    public void setServices(Set<Service> services) {
        this.services = services;
    }
}