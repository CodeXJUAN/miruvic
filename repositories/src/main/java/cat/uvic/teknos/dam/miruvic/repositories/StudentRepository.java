package cat.uvic.teknos.dam.miruvic.repositories;

import cat.uvic.teknos.dam.miruvic.model.Student;

import java.util.List;

public interface StudentRepository extends Repository<Integer, Student> {
    List<Student> findByName(String name);

    List<Student> findByEmail(String email);
    
    List<Student> findByPhone(String phone_number);
}