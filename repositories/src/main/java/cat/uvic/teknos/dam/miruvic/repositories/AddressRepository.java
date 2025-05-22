package cat.uvic.teknos.dam.miruvic.repositories;

public interface AddressRepository<Address> extends Repository<Integer, Address> {
    Address findByCity(String city);

    Address findByPostalCode(String postalCode);

    Address findByCountry(String country);
}