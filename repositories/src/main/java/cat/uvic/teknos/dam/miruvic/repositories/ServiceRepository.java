package cat.uvic.teknos.dam.miruvic.repositories;

public interface ServiceRepository<Service> extends Repository<Integer, Service> {
    Service findByName(String name);

    Service findByType(String type);
}