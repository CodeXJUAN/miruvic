package cat.uvic.teknos.dam.miruvic.model;

import java.util.Set;

public interface Student {
    public int getId();

    public void setId(int id);

    public String getFirstName();

    public void setFirstName(String firstName);

    public String getLastName();

    public void setLastName(String lastName);

    public String getEmail();

    public void setEmail(String email);

    public String getPasswordHash();

    public void setPasswordHash(String passwordHash);

    public String getPhoneNumber();

    public void setPhoneNumber(String phoneNumber);

    public Set<Address> getAddress();

    public void setAddress(Address address);
}