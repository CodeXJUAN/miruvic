package cat.uvic.teknos.dam.miruvic.jpa.repository;

import cat.uvic.teknos.dam.miruvic.jpa.model.JpaReservation;
import cat.uvic.teknos.dam.miruvic.jpa.model.JpaReservationService;
import cat.uvic.teknos.dam.miruvic.jpa.model.JpaService;
import cat.uvic.teknos.dam.miruvic.jpa.model.ReservationServiceId;
import cat.uvic.teknos.dam.miruvic.model.ReservationService;
import cat.uvic.teknos.dam.miruvic.repositories.ReservationServiceRepository;
import cat.uvic.teknos.dam.miruvic.jpa.exceptions.RepositoryException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JpaReservationServiceRepository implements ReservationServiceRepository {

    private final EntityManagerFactory entityManagerFactory;

    public JpaReservationServiceRepository(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public void save(ReservationService reservationService) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();
            try {
                Integer reservationId = reservationService.getReservation() != null ? reservationService.getReservation().getId() : null;
                Integer serviceId = reservationService.getService() != null ? reservationService.getService().getId() : null;

                if (reservationId != null && serviceId != null) {
                    JpaReservation jpaReservation = entityManager.find(JpaReservation.class, reservationId);
                    JpaService jpaService = entityManager.find(JpaService.class, serviceId);

                    if (jpaReservation != null && jpaService != null) {
                        ReservationServiceId id = new ReservationServiceId();
                        id.setReservationId(reservationId);
                        id.setServiceId(serviceId);

                        JpaReservationService existingEntity = entityManager.find(JpaReservationService.class, id);

                        if (existingEntity == null) {
                            JpaReservationService jpaReservationService = new JpaReservationService();
                            jpaReservationService.setId(id);
                            jpaReservationService.setReservation(jpaReservation);
                            jpaReservationService.setService(jpaService);
                            jpaReservationService.setQuantity(reservationService.getQuantity());

                            entityManager.persist(jpaReservationService);
                        } else {
                            existingEntity.setQuantity(reservationService.getQuantity());
                            entityManager.merge(existingEntity);
                        }
                    }
                }
                entityManager.getTransaction().commit();
            } catch (Exception e) {
                if (entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().rollback();
                }
                throw new RepositoryException("Error saving reservation service", e);
            }
        }
    }

    @Override
    public void delete(ReservationService reservationService) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();
            try {
                Integer reservationId = reservationService.getReservation() != null ? reservationService.getReservation().getId() : null;
                Integer serviceId = reservationService.getService() != null ? reservationService.getService().getId() : null;

                if (reservationId != null && serviceId != null) {
                    ReservationServiceId id = new ReservationServiceId();
                    id.setReservationId(reservationId);
                    id.setServiceId(serviceId);

                    JpaReservationService jpaReservationService = entityManager.find(JpaReservationService.class, id);
                    if (jpaReservationService != null) {
                        entityManager.remove(jpaReservationService);
                    }
                }
                entityManager.getTransaction().commit();
            } catch (Exception e) {
                if (entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().rollback();
                }
                throw new RepositoryException("Error deleting reservation service", e);
            }
        }
    }

    @Override
    public ReservationService get(Integer id) {
        throw new RepositoryException("Cannot get ReservationService by single ID. Use findByReservationIdAndServiceId instead.");
    }

    @Override
    public Set<ReservationService> getAll() {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            TypedQuery<JpaReservationService> query = entityManager.createQuery(
                    "SELECT rs FROM JpaReservationService rs", JpaReservationService.class);
            List<JpaReservationService> reservationServices = query.getResultList();
            return new HashSet<>(reservationServices);
        }
    }

    @Override
    public List<ReservationService> findByReservationId(Integer reservationId) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            TypedQuery<JpaReservationService> query = entityManager.createQuery(
                    "SELECT rs FROM JpaReservationService rs JOIN rs.reservation r WHERE r.id = :reservationId",
                    JpaReservationService.class);
            query.setParameter("reservationId", reservationId);
            return query.getResultList().stream().map(rs -> (ReservationService) rs).toList();
        }
    }

    @Override
    public List<ReservationService> findByServiceId(Integer serviceId) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            TypedQuery<JpaReservationService> query = entityManager.createQuery(
                    "SELECT rs FROM JpaReservationService rs JOIN rs.service s WHERE s.id = :serviceId",
                    JpaReservationService.class);
            query.setParameter("serviceId", serviceId);
            return query.getResultList().stream().map(rs -> (ReservationService) rs).toList();
        }
    }
}
