package cat.uvic.teknos.dam.miruvic.repositories;

import cat.uvic.teknos.dam.miruvic.model.Address;

public interface AddressRepository extends Repository<Integer, Address> {
    Address findByCity(String city);

    Address findByPostalCode(String postalCode);

    Address findByCountry(String country);
}