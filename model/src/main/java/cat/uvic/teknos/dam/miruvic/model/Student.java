package cat.uvic.teknos.dam.miruvic.model;

import java.util.Set;

public interface Student {
    Integer getId();
    String getFirstName();
    String getLastName();
    String getEmail();
    String getPasswordHash();
    String getPhoneNumber();
    Set<Address> getAddress();

    void setId(Integer id);
    void setFirstName(String firstName);
    void setLastName(String lastName);
    void setEmail(String email);
    void setPasswordHash(String passwordHash);
    void setPhoneNumber(String phoneNumber);
    void setAddress(Set<Address> address_id);

}