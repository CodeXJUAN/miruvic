package cat.uvic.teknos.dam.miruvic.repositories;

import java.util.Set;

public interface Repository<K, V> {
    void save(V value);

    void delete(V Value);

    V get(K id);

    Set<V> getAll();
}