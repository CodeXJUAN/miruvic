package cat.uvic.teknos.dam.miruvic;

public interface ServiceRepository<Service> extends Repository<Integer, Service> {
    Service findByName(String name);

    java.util.List<Service> findByType(String type);
}