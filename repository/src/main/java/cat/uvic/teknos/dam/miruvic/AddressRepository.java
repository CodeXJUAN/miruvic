package cat.uvic.teknos.dam.miruvic;

public interface AddressRepository extends Repository<Integer, Address> {
    // Puedes añadir métodos específicos para Address aquí si es necesario
    java.util.List<Address> findByCity(String city);

    java.util.List<Address> findByPostalCode(String postalCode);

    java.util.List<Address> findByCountry(String country);
}