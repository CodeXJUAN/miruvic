package cat.uvic.teknos.dam.miruvic.jpa.repository;

import cat.uvic.teknos.dam.miruvic.jpa.model.JpaService;
import cat.uvic.teknos.dam.miruvic.model.Service;
import cat.uvic.teknos.dam.miruvic.repositories.ServiceRepository;
import cat.uvic.teknos.dam.miruvic.jpa.exceptions.RepositoryException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JpaServiceRepository implements ServiceRepository {

    private final EntityManagerFactory entityManagerFactory;

    public JpaServiceRepository(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public void save(Service service) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();
            try {
                if (service.getId() == null || service.getId() == 0) {
                    JpaService jpaService = new JpaService();
                    jpaService.setServiceName(service.getServiceName());
                    jpaService.setDescription(service.getDescription());
                    jpaService.setPrice(service.getPrice());

                    entityManager.persist(jpaService);
                    service.setId(jpaService.getId());
                } else {
                    JpaService jpaService = entityManager.find(JpaService.class, service.getId());
                    if (jpaService != null) {
                        jpaService.setServiceName(service.getServiceName());
                        jpaService.setDescription(service.getDescription());
                        jpaService.setPrice(service.getPrice());

                        entityManager.merge(jpaService);
                    }
                }
                entityManager.getTransaction().commit();
            } catch (Exception e) {
                if (entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().rollback();
                }
                throw new RepositoryException("Error saving service", e);
            }
        }
    }

    @Override
    public void delete(Service service) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();
            try {
                JpaService jpaService = entityManager.find(JpaService.class, service.getId());
                if (jpaService != null) {
                    entityManager.remove(jpaService);
                }
                entityManager.getTransaction().commit();
            } catch (Exception e) {
                if (entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().rollback();
                }
                throw new RepositoryException("Error deleting service", e);
            }
        }
    }

    @Override
    public Service get(Integer id) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            return entityManager.find(JpaService.class, id);
        }
    }

    @Override
    public Set<Service> getAll() {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            TypedQuery<JpaService> query = entityManager.createQuery(
                    "SELECT s FROM JpaService s", JpaService.class);
            List<JpaService> services = query.getResultList();
            return new HashSet<>(services);
        }
    }

    @Override
    public List<Service> findByName(String name) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            TypedQuery<JpaService> query = entityManager.createQuery(
                    "SELECT s FROM JpaService s WHERE s.serviceName LIKE :name", JpaService.class);
            query.setParameter("name", "%" + name + "%");
            return query.getResultList().stream().map(service -> (Service) service).toList();
        }
    }

    @Override
    public List<Service> findByType(String type) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            TypedQuery<JpaService> query = entityManager.createQuery(
                    "SELECT s FROM JpaService s WHERE s.description LIKE :type", JpaService.class);
            query.setParameter("type", "%" + type + "%");
            return query.getResultList().stream().map(service -> (Service) service).toList();
        }
    }
}