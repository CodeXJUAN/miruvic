package cat.uvic.teknos.dam.miruvic.repositories;

public interface ServiceRepository extends Repository<Integer, Service> {
    Service findByName(String name);

    java.util.List<Service> findByType(String type);
}