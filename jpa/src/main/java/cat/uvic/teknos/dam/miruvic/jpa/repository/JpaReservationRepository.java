package cat.uvic.teknos.dam.miruvic.jpa.repository;

import cat.uvic.teknos.dam.miruvic.jpa.model.JpaReservation;
import cat.uvic.teknos.dam.miruvic.jpa.model.JpaRoom;
import cat.uvic.teknos.dam.miruvic.jpa.model.JpaStudent;
import cat.uvic.teknos.dam.miruvic.model.Reservation;
import cat.uvic.teknos.dam.miruvic.repositories.ReservationRepository;
import cat.uvic.teknos.dam.miruvic.jpa.exceptions.RepositoryException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JpaReservationRepository implements ReservationRepository {

    private final EntityManagerFactory entityManagerFactory;

    public JpaReservationRepository(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public void save(Reservation reservation) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();
            try {
                if (reservation.getId() == null || reservation.getId() == 0) {
                    JpaReservation jpaReservation = new JpaReservation();
                    jpaReservation.setStartDate(reservation.getStartDate());
                    jpaReservation.setEndDate(reservation.getEndDate());
                    jpaReservation.setStatus(reservation.getStatus());

                    if (reservation.getStudent() != null && reservation.getStudent().getId() != null) {
                        JpaStudent jpaStudent = entityManager.find(JpaStudent.class, reservation.getStudent().getId());
                        jpaReservation.setStudent(jpaStudent);
                    }
                    if (reservation.getRoom() != null && reservation.getRoom().getId() != null) {
                        JpaRoom jpaRoom = entityManager.find(JpaRoom.class, reservation.getRoom().getId());
                        jpaReservation.setRoom(jpaRoom);
                    }

                    entityManager.persist(jpaReservation);
                    reservation.setId(jpaReservation.getId());
                } else {
                    JpaReservation jpaReservation = entityManager.find(JpaReservation.class, reservation.getId());
                    if (jpaReservation != null) {
                        jpaReservation.setStartDate(reservation.getStartDate());
                        jpaReservation.setEndDate(reservation.getEndDate());
                        jpaReservation.setStatus(reservation.getStatus());

                        if (reservation.getStudent() != null && reservation.getStudent().getId() != null) {
                            JpaStudent jpaStudent = entityManager.find(JpaStudent.class, reservation.getStudent().getId());
                            jpaReservation.setStudent(jpaStudent);
                        } else {
                            jpaReservation.setStudent(null);
                        }
                        if (reservation.getRoom() != null && reservation.getRoom().getId() != null) {
                            JpaRoom jpaRoom = entityManager.find(JpaRoom.class, reservation.getRoom().getId());
                            jpaReservation.setRoom(jpaRoom);
                        } else {
                            jpaReservation.setRoom(null);
                        }

                        entityManager.merge(jpaReservation);
                    }
                }
                entityManager.getTransaction().commit();
            } catch (Exception e) {
                if (entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().rollback();
                }
                throw new RepositoryException("Error saving reservation", e);
            }
        }
    }

    @Override
    public void delete(Reservation reservation) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();
            try {
                JpaReservation jpaReservation = entityManager.find(JpaReservation.class, reservation.getId());
                if (jpaReservation != null) {
                    entityManager.remove(jpaReservation);
                }
                entityManager.getTransaction().commit();
            } catch (Exception e) {
                if (entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().rollback();
                }
                throw new RepositoryException("Error deleting reservation", e);
            }
        }
    }

    @Override
    public Reservation get(Integer id) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            return entityManager.find(JpaReservation.class, id);
        }
    }

    @Override
    public Set<Reservation> getAll() {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            TypedQuery<JpaReservation> query = entityManager.createQuery(
                    "SELECT r FROM JpaReservation r", JpaReservation.class);
            List<JpaReservation> reservations = query.getResultList();
            return new HashSet<>(reservations);
        }
    }

    @Override
    public List<Reservation> findByDate(LocalDate date) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            TypedQuery<JpaReservation> query = entityManager.createQuery(
                    "SELECT r FROM JpaReservation r WHERE r.startDate <= :date AND r.endDate >= :date",
                    JpaReservation.class);
            query.setParameter("date", date);
            return query.getResultList().stream().map(reservation -> (Reservation) reservation).toList();
        }
    }

    @Override
    public List<Reservation> findByStudentId(Integer studentId) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            TypedQuery<JpaReservation> query = entityManager.createQuery(
                    "SELECT r FROM RESERVATION r JOIN r.student s WHERE s.id = :studentId",
                    JpaReservation.class);
            query.setParameter("studentId", studentId);
            return query.getResultList().stream().map(reservation -> (Reservation) reservation).toList();
        }
    }
}
