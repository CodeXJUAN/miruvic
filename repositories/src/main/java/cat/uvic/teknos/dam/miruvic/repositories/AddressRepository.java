package cat.uvic.teknos.dam.miruvic.repositories;

public interface AddressRepository<Address> extends Repository<Integer, Address> {
    java.util.List<Address> findByCity(String city);

    java.util.List<Address> findByPostalCode(String postalCode);

    java.util.List<Address> findByCountry(String country);
}