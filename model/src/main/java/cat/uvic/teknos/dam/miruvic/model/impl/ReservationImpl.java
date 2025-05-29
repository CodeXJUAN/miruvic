package cat.uvic.teknos.dam.miruvic.model.impl;

import cat.uvic.teknos.dam.miruvic.model.Student;
import cat.uvic.teknos.dam.miruvic.model.Room;
import cat.uvic.teknos.dam.miruvic.model.Service;
import cat.uvic.teknos.dam.miruvic.model.Reservation;

import java.time.LocalDate;
import java.util.Set;

public class ReservationImpl implements Reservation {
    private Integer id;
    private Set<Student> student;
    private Set<Room> room;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private Set<Service> services;



    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public Set<Student> getStudent() {
        return Set.of();
    }

    @Override
    public void setStudent(Set<Student> student) {
        this.student = student;
    }

    @Override
    public Set<Room> getRoom() {
        return Set.of();
    }

    @Override
    public void setRoom(Set<Room> room) {
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
    public String getStatus() {
        return status;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public Set<Service> getServices() {
        return Set.of();
    }

    @Override
    public void setServices(Set<Service> services) {
        this.services = services;
    }
}