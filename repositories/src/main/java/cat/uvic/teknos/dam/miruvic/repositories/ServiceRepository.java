package cat.uvic.teknos.dam.miruvic.repositories;

import cat.uvic.teknos.dam.miruvic.model.Service;

public interface ServiceRepository extends Repository<Integer, Service> {
    Service findByName(String name);

    Service findByType(String type);
}