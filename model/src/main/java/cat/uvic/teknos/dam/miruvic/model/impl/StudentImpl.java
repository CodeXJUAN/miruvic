package cat.uvic.teknos.dam.miruvic.model.impl;

import cat.uvic.teknos.dam.miruvic.model.Address;
import cat.uvic.teknos.dam.miruvic.model.Reservation;
import cat.uvic.teknos.dam.miruvic.model.Student;
import java.util.Set;

public class StudentImpl implements Student {
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private String passwordHash;
    private String phoneNumber;
    private Set<Address> address;
    private Set<Reservation> reservations;

    @Override
    public Integer getId() { return id; }

    @Override
    public void setId(Integer id) { this.id = id; }

    @Override
    public String getFirstName() { return firstName; }

    @Override
    public void setFirstName(String firstName) { this.firstName = firstName; }

    @Override
    public String getLastName() { return lastName; }

    @Override
    public void setLastName(String lastName) { this.lastName = lastName; }

    @Override
    public String getEmail() { return email; }

    @Override
    public void setEmail(String email) { this.email = email; }

    @Override
    public String getPasswordHash() { return passwordHash; }

    @Override
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    @Override
    public String getPhoneNumber() { return phoneNumber; }

    @Override
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    @Override
    public Set<Address> getAddress() { return Set.of(); }

    @Override
    public void setAddress(Set<Address> address) { this.address = address; }

    @Override
    public Set<Reservation> getReservations() { return Set.of(); }

    @Override
    public void setReservations(Set<Reservation> reservations) { this.reservations = reservations;  }
}