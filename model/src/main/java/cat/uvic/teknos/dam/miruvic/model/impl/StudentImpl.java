package cat.uvic.teknos.dam.miruvic.model.impl;

import java.util.Set;
import cat.uvic.teknos.dam.miruvic.model.Address;
import cat.uvic.teknos.dam.miruvic.model.Student;

public class StudentImpl implements Student {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String passwordHash;
    private String phoneNumber;
    private Set<Address> address_id;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getPasswordHash() {
        return passwordHash;
    }

    @Override
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    @Override
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public Set<Address> getAddress() {
        return address_id != null ? address_id : Set.of();
    }

    @Override
    public void setAddress(Address address) {
        this.address_id = address != null ? Set.of(address) : Set.of();
    }
}