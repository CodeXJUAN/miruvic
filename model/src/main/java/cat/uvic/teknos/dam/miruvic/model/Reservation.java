package cat.uvic.teknos.dam.miruvic.model;

import cat.uvic.teknos.dam.miruvic.model.ReservationStatus;
import java.time.LocalDate;
import java.util.Set;

public interface Reservation {
    public int getId();

    public void setId(int id);

    public Set<Student> getStudent();

    public void setStudent(Student student);

    public Set<Room> getRoom();

    public void setRoom(Room room);

    public LocalDate getStartDate();

    public void setStartDate(LocalDate startDate);

    public LocalDate getEndDate();

    public void setEndDate(LocalDate endDate);

    public ReservationStatus getStatus();
    
    public void setStatus(ReservationStatus status);

    public Set<Service> getServices();

    public void setServices(Set<Service> services);
}