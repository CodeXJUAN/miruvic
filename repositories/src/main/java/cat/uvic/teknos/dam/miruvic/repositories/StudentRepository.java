package cat.uvic.teknos.dam.miruvic.repositories;

public interface StudentRepository<Student> extends Repository<Integer, Student> {
    Student findByName(String name);

    Student findByEmail(String email);
    
    Student findByPhone(String phone_number);
}