package cat.uvic.teknos.dam.miruvic.jpa.repository;

import cat.uvic.teknos.dam.miruvic.jpa.model.JpaAddress;
import cat.uvic.teknos.dam.miruvic.jpa.model.JpaStudent;
import cat.uvic.teknos.dam.miruvic.model.Student;
import cat.uvic.teknos.dam.miruvic.repositories.StudentRepository;
import cat.uvic.teknos.dam.miruvic.jpa.exceptions.RepositoryException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JpaStudentRepository implements StudentRepository {

    private final EntityManagerFactory entityManagerFactory;

    public JpaStudentRepository(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public void save(Student student) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();
            try {
                if (student.getId() == null || student.getId() == 0) {
                    JpaStudent jpaStudent = new JpaStudent();
                    jpaStudent.setFirstName(student.getFirstName());
                    jpaStudent.setLastName(student.getLastName());
                    jpaStudent.setEmail(student.getEmail());
                    jpaStudent.setPhoneNumber(student.getPhoneNumber());
                    jpaStudent.setPasswordHash(student.getPasswordHash()); // <-- Añade esta línea

                    if (student.getAddress() != null && student.getAddress().getId() != null) {
                        JpaAddress jpaAddress = entityManager.find(JpaAddress.class, student.getAddress().getId());
                        jpaStudent.setAddress(jpaAddress);
                    }

                    entityManager.persist(jpaStudent);
                    student.setId(jpaStudent.getId());
                } else {
                    JpaStudent jpaStudent = entityManager.find(JpaStudent.class, student.getId());
                    if (jpaStudent != null) {
                        jpaStudent.setFirstName(student.getFirstName());
                        jpaStudent.setLastName(student.getLastName());
                        jpaStudent.setEmail(student.getEmail());
                        jpaStudent.setPhoneNumber(student.getPhoneNumber());
                        jpaStudent.setPasswordHash(student.getPasswordHash()); // <-- Añade esta línea

                        if (student.getAddress() != null && student.getAddress().getId() != null) {
                            JpaAddress jpaAddress = entityManager.find(JpaAddress.class, student.getAddress().getId());
                            jpaStudent.setAddress(jpaAddress);
                        } else {
                            jpaStudent.setAddress(null);
                        }

                        entityManager.merge(jpaStudent);
                    }
                }
                entityManager.getTransaction().commit();
            } catch (Exception e) {
                if (entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().rollback();
                }
                throw new RepositoryException("Error saving student", e);
            }
        }
    }

    @Override
    public void delete(Student student) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            entityManager.getTransaction().begin();
            try {
                JpaStudent jpaStudent = entityManager.find(JpaStudent.class, student.getId());
                if (jpaStudent != null) {
                    entityManager.remove(jpaStudent);
                }
                entityManager.getTransaction().commit();
            } catch (Exception e) {
                if (entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().rollback();
                }
                throw new RepositoryException("Error deleting student", e);
            }
        }
    }

    @Override
    public Student get(Integer id) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            return entityManager.find(JpaStudent.class, id);
        }
    }

    @Override
    public Set<Student> getAll() {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            TypedQuery<JpaStudent> query = entityManager.createQuery(
                    "SELECT s FROM JpaStudent s", JpaStudent.class);
            List<JpaStudent> students = query.getResultList();
            return new HashSet<>(students);
        }
    }

    @Override
    public List<Student> findByName(String name) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            TypedQuery<JpaStudent> query = entityManager.createQuery(
                    "SELECT s FROM JpaStudent s WHERE s.firstName LIKE :name", JpaStudent.class);
            query.setParameter("name", "%" + name + "%");
            return query.getResultList().stream().map(student -> (Student) student).toList();
        }
    }


    @Override
    public List<Student> findByEmail(String email) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            TypedQuery<JpaStudent> query = entityManager.createQuery(
                    "SELECT s FROM JpaStudent s WHERE s.email = :email", JpaStudent.class);
            query.setParameter("email", email);
            return query.getResultList().stream().map(student -> (Student) student).toList();
        }
    }

    @Override
    public List<Student> findByPhone(String phone) {
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            TypedQuery<JpaStudent> query = entityManager.createQuery(
                    "SELECT s FROM JpaStudent s WHERE s.phoneNumber = :phone", JpaStudent.class);
            query.setParameter("phone", phone);
            return query.getResultList().stream().map(student -> (Student) student).toList();
        }
    }
}
