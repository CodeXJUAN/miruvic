package cat.uvic.teknos.dam.miruvic;

public interface Student {
    int getId();

    void setId(int id);

    String getFirstName();

    void setFirstName(String firstName);

    String getLastName();

    void setLastName(String lastName);

    String getEmail();

    void setEmail(String email);

    String getPasswordHash();

    void setPasswordHash(String passwordHash);

    String getPhoneNumber();

    void setPhoneNumber(String phoneNumber);

    Address getAddress();

    void setAddress(Address address);
}