package cat.uvic.teknos.dam.miruvic.repositories;

public interface StudentRepository<Student> extends Repository<Integer, Student> {

    java.util.List<Student> findByName(String name);

    java.util.List<Student> findByEmail(String email);

    java.util.List<Student> findByMatricula(String matricula);
}