package cat.uvic.teknos.dam.miruvic.repositories;

import cat.uvic.teknos.dam.miruvic.model.Service;

import java.util.List;

public interface ServiceRepository extends Repository<Integer, Service> {
    List<Service> findByName(String name);

    List<Service> findByType(String type);
}