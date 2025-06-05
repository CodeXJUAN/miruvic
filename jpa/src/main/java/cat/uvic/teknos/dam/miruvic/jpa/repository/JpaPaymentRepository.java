package cat.uvic.teknos.dam.miruvic.jpa.repository;

import cat.uvic.teknos.dam.miruvic.jpa.model.JpaPayment;
import cat.uvic.teknos.dam.miruvic.jpa.model.JpaReservation;
import cat.uvic.teknos.dam.miruvic.model.Payment;
import cat.uvic.teknos.dam.miruvic.repositories.PaymentRepository;
import cat.uvic.teknos.dam.miruvic.jpa.exceptions.RepositoryException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JpaPaymentRepository implements PaymentRepository {

    private final EntityManagerFactory entityManagerFactory;

    public JpaPaymentRepository(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public void save(Payment payment) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();
            try {
                if (payment.getId() == null || payment.getId() == 0) {
                    JpaPayment jpaPayment = new JpaPayment();
                    jpaPayment.setAmount(payment.getAmount());
                    jpaPayment.setPaymentDate(payment.getPaymentDate());
                    jpaPayment.setPaymentMethod(payment.getPaymentMethod());
                    jpaPayment.setStatus(payment.getStatus());

                    if (payment.getReservation() != null && payment.getReservation().getId() != null) {
                        JpaReservation jpaReservation = entityManager.find(JpaReservation.class, payment.getReservation().getId());
                        jpaPayment.setReservation(jpaReservation);
                    }

                    entityManager.persist(jpaPayment);
                    payment.setId(jpaPayment.getId());
                } else {
                    JpaPayment jpaPayment = entityManager.find(JpaPayment.class, payment.getId());
                    if (jpaPayment != null) {
                        jpaPayment.setAmount(payment.getAmount());
                        jpaPayment.setPaymentDate(payment.getPaymentDate());
                        jpaPayment.setPaymentMethod(payment.getPaymentMethod());
                        jpaPayment.setStatus(payment.getStatus());

                        if (payment.getReservation() != null && payment.getReservation().getId() != null) {
                            JpaReservation jpaReservation = entityManager.find(JpaReservation.class, payment.getReservation().getId());
                            jpaPayment.setReservation(jpaReservation);
                        } else {
                            jpaPayment.setReservation(null);
                        }

                        entityManager.merge(jpaPayment);
                    }
                }
                entityManager.getTransaction().commit();
            } catch (Exception e) {
                if (entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().rollback();
                }
                throw new RepositoryException("Error saving payment", e);
            }
        }
    }

    @Override
    public void delete(Payment payment) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();
            try {
                JpaPayment jpaPayment = entityManager.find(JpaPayment.class, payment.getId());
                if (jpaPayment != null) {
                    entityManager.remove(jpaPayment);
                }
                entityManager.getTransaction().commit();
            } catch (Exception e) {
                if (entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().rollback();
                }
                throw new RepositoryException("Error deleting payment", e);
            }
        }
    }

    @Override
    public Payment get(Integer id) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            return entityManager.find(JpaPayment.class, id);
        }
    }

    @Override
    public Set<Payment> getAll() {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            TypedQuery<JpaPayment> query = entityManager.createQuery(
                    "SELECT p FROM JpaPayment p", JpaPayment.class);
            List<JpaPayment> payments = query.getResultList();
            return new HashSet<>(payments);
        }
    }

    @Override
    public List<Payment> findByMethod(String method) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            TypedQuery<JpaPayment> query = entityManager.createQuery(
                    "SELECT p FROM JpaPayment p WHERE p.paymentMethod = :method", JpaPayment.class);
            query.setParameter("method", method);
            return query.getResultList().stream().map(payment -> (Payment) payment).toList();
        }
    }

    @Override
    public List<Payment> findByAmountRange(double minAmount, double maxAmount) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            TypedQuery<JpaPayment> query = entityManager.createQuery(
                    "SELECT p FROM JpaPayment p WHERE p.amount BETWEEN :minAmount AND :maxAmount", JpaPayment.class);
            query.setParameter("minAmount", minAmount);
            query.setParameter("maxAmount", maxAmount);
            return query.getResultList().stream().map(payment -> (Payment) payment).toList();
        }
    }
}
