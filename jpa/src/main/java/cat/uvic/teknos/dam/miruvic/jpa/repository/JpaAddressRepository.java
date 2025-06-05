package cat.uvic.teknos.dam.miruvic.jpa.repository;

import cat.uvic.teknos.dam.miruvic.jpa.model.JpaAddress;
import cat.uvic.teknos.dam.miruvic.model.Address;
import cat.uvic.teknos.dam.miruvic.repositories.AddressRepository;
import cat.uvic.teknos.dam.miruvic.jpa.exceptions.RepositoryException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JpaAddressRepository implements AddressRepository {

    private final EntityManagerFactory entityManagerFactory;

    public JpaAddressRepository(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public void save(Address address) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();
            try {
                if (address.getId() == null || address.getId() == 0) {
                    JpaAddress jpaAddress = new JpaAddress();
                    jpaAddress.setStreet(address.getStreet());
                    jpaAddress.setCity(address.getCity());
                    jpaAddress.setState(address.getState());
                    jpaAddress.setZipCode(address.getZipCode());
                    jpaAddress.setCountry(address.getCountry());

                    entityManager.persist(jpaAddress);
                    address.setId(jpaAddress.getId());
                } else {
                    JpaAddress jpaAddress = entityManager.find(JpaAddress.class, address.getId());
                    if (jpaAddress != null) {
                        jpaAddress.setStreet(address.getStreet());
                        jpaAddress.setCity(address.getCity());
                        jpaAddress.setState(address.getState());
                        jpaAddress.setZipCode(address.getZipCode());
                        jpaAddress.setCountry(address.getCountry());

                        entityManager.merge(jpaAddress);
                    }
                }
                entityManager.getTransaction().commit();
            } catch (Exception e) {
                if (entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().rollback();
                }
                throw new RepositoryException("Error saving address", e);
            }
        }
    }

    @Override
    public void delete(Address address) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();
            try {
                JpaAddress jpaAddress = entityManager.find(JpaAddress.class, address.getId());
                if (jpaAddress != null) {
                    entityManager.remove(jpaAddress);
                }
                entityManager.getTransaction().commit();
            } catch (Exception e) {
                if (entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().rollback();
                }
                throw new RepositoryException("Error deleting address", e);
            }
        }
    }

    @Override
    public Address get(Integer id) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            return entityManager.find(JpaAddress.class, id);
        }
    }

    @Override
    public Set<Address> getAll() {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            List<JpaAddress> jpaAddresses = entityManager
                    .createQuery("FROM JpaAddress", JpaAddress.class)
                    .getResultList();
            return new HashSet<>(jpaAddresses.stream()
                    .map(jpa -> (Address) jpa)
                    .toList());
        }
    }

    @Override
    public List<Address> findByCity(String city) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            List<JpaAddress> jpaAddresses = entityManager
                    .createQuery("FROM JpaAddress WHERE city = :city", JpaAddress.class)
                    .setParameter("city", city)
                    .getResultList();
            return jpaAddresses.stream()
                    .map(jpa -> (Address) jpa)
                    .toList();
        }
    }

    @Override
    public List<Address> findByPostalCode(String postalCode) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            List<JpaAddress> jpaAddresses = entityManager
                    .createQuery("FROM JpaAddress WHERE zipCode = :zipCode", JpaAddress.class)
                    .setParameter("zipCode", postalCode)
                    .getResultList();
            return jpaAddresses.stream()
                    .map(jpa -> (Address) jpa)
                    .toList();
        }
    }

    @Override
    public List<Address> findByCountry(String country) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            List<JpaAddress> jpaAddresses = entityManager
                    .createQuery("FROM JpaAddress WHERE country = :country", JpaAddress.class)
                    .setParameter("country", country)
                    .getResultList();
            return jpaAddresses.stream()
                    .map(jpa -> (Address) jpa)
                    .toList();
        }
    }
}