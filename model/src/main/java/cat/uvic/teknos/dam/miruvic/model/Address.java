package cat.uvic.teknos.dam.miruvic.model;

public interface Address {
    public int getId();

    public void setId(int id);

    public String getStreet();

    public void setStreet(String street);

    public String getCity();

    public void setCity(String city);

    public String getState();

    public void setState(String state);

    public String getZipCode();

    public void setZipCode(String zipCode);

    public String getCountry();

    public void setCountry(String country);
}