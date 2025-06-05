package cat.uvic.teknos.dam.miruvic.jpa.repository;

import cat.uvic.teknos.dam.miruvic.jpa.model.JpaRoom;
import cat.uvic.teknos.dam.miruvic.model.Room;
import cat.uvic.teknos.dam.miruvic.repositories.RoomRepository;
import cat.uvic.teknos.dam.miruvic.jpa.exceptions.RepositoryException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JpaRoomRepository implements RoomRepository {

    private final EntityManagerFactory entityManagerFactory;

    public JpaRoomRepository(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public void save(Room room) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();
            try {
                if (room.getId() == null || room.getId() == 0) {
                    JpaRoom jpaRoom = new JpaRoom();
                    jpaRoom.setRoomNumber(room.getRoomNumber());
                    jpaRoom.setFloor(room.getFloor());
                    jpaRoom.setCapacity(room.getCapacity());
                    jpaRoom.setType(room.getType());
                    jpaRoom.setPrice(room.getPrice());

                    entityManager.persist(jpaRoom);
                    room.setId(jpaRoom.getId());
                } else {
                    JpaRoom jpaRoom = entityManager.find(JpaRoom.class, room.getId());
                    if (jpaRoom != null) {
                        jpaRoom.setRoomNumber(room.getRoomNumber());
                        jpaRoom.setFloor(room.getFloor());
                        jpaRoom.setCapacity(room.getCapacity());
                        jpaRoom.setType(room.getType());
                        jpaRoom.setPrice(room.getPrice());

                        entityManager.merge(jpaRoom);
                    }
                }
                entityManager.getTransaction().commit();
            } catch (Exception e) {
                if (entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().rollback();
                }
                throw new RepositoryException("Error saving room", e);
            }
        }
    }

    @Override
    public void delete(Room room) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();
            try {
                JpaRoom jpaRoom = entityManager.find(JpaRoom.class, room.getId());
                if (jpaRoom != null) {
                    entityManager.remove(jpaRoom);
                }
                entityManager.getTransaction().commit();
            } catch (Exception e) {
                if (entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().rollback();
                }
                throw new RepositoryException("Error deleting room", e);
            }
        }
    }

    @Override
    public Room get(Integer id) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            return entityManager.find(JpaRoom.class, id);
        }
    }

    @Override
    public Set<Room> getAll() {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            TypedQuery<JpaRoom> query = entityManager.createQuery(
                    "SELECT r FROM JpaRoom r", JpaRoom.class);
            List<JpaRoom> rooms = query.getResultList();
            return new HashSet<>(rooms);
        }
    }

    @Override
    public List<Room> findByNumber(String number) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            TypedQuery<JpaRoom> query = entityManager.createQuery(
                    "SELECT r FROM JpaRoom r WHERE r.roomNumber = :number", JpaRoom.class);
            query.setParameter("number", number);
            return query.getResultList().stream().map(room -> (Room) room).toList();
        }
    }

    @Override
    public List<Room> findByType(String type) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            TypedQuery<JpaRoom> query = entityManager.createQuery(
                    "SELECT r FROM JpaRoom r WHERE r.type = :type", JpaRoom.class);
            query.setParameter("type", type);
            return query.getResultList().stream().map(room -> (Room) room).toList();
        }
    }
}