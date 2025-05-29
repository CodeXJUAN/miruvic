package cat.uvic.teknos.dam.miruvic.model;

public interface Address {
    Integer getId();
    String getStreet();
    String getCity();
    String getState();
    String getZipCode();
    String getCountry();



    void setId(Integer id);
    void setStreet(String street);
    void setCity(String city);
    void setState(String state);
    void setZipCode(String zipCode);
    void setCountry(String country);
}