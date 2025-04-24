package miruvic;

public interface Address {
    int getId();

    void setId(int id);

    String getStreet();

    void setStreet(String street);

    String getCity();

    void setCity(String city);

    String getState();

    void setState(String state);

    String getZipCode();

    void setZipCode(String zipCode);

    String getCountry();

    void setCountry(String country);
}