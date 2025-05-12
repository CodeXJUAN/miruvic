package cat.uvic.teknos.dam.ruvic.jdbc.classes;

public class Address {
    private int id_addresses;
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String country;

	public int getId() {
        return id_addresses;
    }

    public void setId(int id) {
        this.id_addresses = id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}