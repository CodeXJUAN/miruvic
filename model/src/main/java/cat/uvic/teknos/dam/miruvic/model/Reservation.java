package cat.uvic.teknos.dam.miruvic.model;

import java.time.LocalDate;
import java.util.Set;

public interface Reservation {
    Integer getId();
    Set<Student> getStudent();
    Set<Room> getRoom();
    LocalDate getStartDate();
    LocalDate getEndDate();
    String getStatus();

    void setId(Integer id);
    void setStudent(Set<Student> student);
    void setRoom(Set<Room> room);
    void setStartDate(LocalDate startDate);
    void setEndDate(LocalDate endDate);
    void setStatus(String status);
}