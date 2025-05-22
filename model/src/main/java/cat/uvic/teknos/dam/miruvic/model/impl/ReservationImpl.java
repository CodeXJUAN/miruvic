package cat.uvic.teknos.dam.miruvic.impl;

import cat.uvic.teknos.dam.miruvic.Student;
import cat.uvic.teknos.dam.miruvic.Room;
import cat.uvic.teknos.dam.miruvic.Service;
import java.time.LocalDate;
import java.util.Set;

public class ReservationImpl implements cat.uvic.teknos.dam.miruvic.Reservation {
    private int id;
    private Student student;
    private Room room;
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
    public Student getStudent() {
        return student;
    }

    @Override
    public void setStudent(Student student) {
        this.student = student;
    }

    @Override
    public Room getRoom() {
        return room;
    }

    @Override
    public void setRoom(Room room) {
        this.room = room;
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
