package cat.uvic.teknos.dam.miruvic.model;

public interface Student {
    Integer getId();
    String getFirstName();
    String getLastName();
    String getEmail();
    String getPasswordHash();
    String getPhoneNumber();
    Address getAddress();
    Reservation getReservation();

    void setId(Integer id);
    void setFirstName(String firstName);
    void setLastName(String lastName);
    void setEmail(String email);
    void setPasswordHash(String passwordHash);
    void setPhoneNumber(String phoneNumber);
    void setAddress(Address address);
    void setReservation(Reservation reservation);
}
