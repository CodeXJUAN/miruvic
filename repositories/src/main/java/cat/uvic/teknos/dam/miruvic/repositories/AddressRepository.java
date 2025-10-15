package cat.uvic.teknos.dam.miruvic.repositories;

import cat.uvic.teknos.dam.miruvic.model.Address;

import java.util.List;

public interface AddressRepository extends Repository<Integer, Address> {
    List<Address> findByCity(String city);

    List<Address> findByPostalCode(String postalCode);

    List<Address> findByCountry(String country);
}