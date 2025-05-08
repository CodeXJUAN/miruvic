package cat.uvic.teknos.dam.miruvic;

public interface StudentRepository extends Repository<Integer, Student> {
    // Puedes añadir métodos específicos para Student aquí si es necesario
    java.util.List<Student> findByName(String name);

    java.util.List<Student> findByEmail(String email);

    java.util.List<Student> findByMatricula(String matricula);
}