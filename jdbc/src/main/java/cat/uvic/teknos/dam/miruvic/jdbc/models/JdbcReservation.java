package cat.uvic.teknos.dam.miruvic.jdbc.models;

import cat.uvic.teknos.dam.miruvic.model.Student;
import cat.uvic.teknos.dam.miruvic.model.Room;
import cat.uvic.teknos.dam.miruvic.model.Service;
import cat.uvic.teknos.dam.miruvic.model.Reservation;
import java.time.LocalDate;
import java.util.Set;

public class JdbcReservation implements Reservation {
    private int id;
    private Set<Student> students;
    private Set<Room> rooms;     
    private LocalDate startDate;
    private LocalDate endDate;
    private ReservationStatus status;
    private Set<Service> services;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public Set<Student> getStudent() {
        return students != null ? students : Set.of();
    }

    @Override
    public void setStudent(Student student) {
        this.students = student != null ? Set.of(student) : Set.of();
    }

    @Override
    public Set<Room> getRoom() {
        return rooms != null ? rooms : Set.of();
    }

    @Override
    public void setRoom(Room room) {
        this.rooms = room != null ? Set.of(room) : Set.of();
    }

    @Override
    public LocalDate getStartDate() {
        return startDate;
    }

    @Override
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    @Override
    public LocalDate getEndDate() {
        return endDate;
    }

    @Override
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @Override
    public ReservationStatus getStatus() {
        return status;
    }

    @Override
    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    @Override
    public Set<Service> getServices() {
        return services;
    }

    @Override
    public void setServices(Set<Service> services) {
        this.services = services;
    }
}
