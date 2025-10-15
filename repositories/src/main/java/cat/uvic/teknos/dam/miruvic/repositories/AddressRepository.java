package cat.uvic.teknos.dam.miruvic.repositories;

import cat.uvic.teknos.dam.miruvic.model.Address;
import java.util.Set;

public interface AddressRepository {
    Address get(int id);
    Set<Address> getAll();
    void save(Address address);
    void delete(Address address);
}