package cat.uvic.teknos.dam.miruvic.model;

import java.time.LocalDate;

public interface Reservation {
    Integer getId();
    Student getStudent();
    Room getRoom();
    LocalDate getStartDate();
    LocalDate getEndDate();
    String getStatus();

    void setId(Integer id);
    void setStudent(Student student);
    void setRoom(Room room);
    void setStartDate(LocalDate startDate);
    void setEndDate(LocalDate endDate);
    void setStatus(String status);
}
