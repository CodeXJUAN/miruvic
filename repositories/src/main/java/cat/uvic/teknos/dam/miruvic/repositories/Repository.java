package cat.uvic.teknos.dam.miruvic.repositories;

import java.util.List;

public interface Repository<K, V> {
    void save(V value);

    void delete(V Value);

    V get(K id);

    List<Student> getAll();
}