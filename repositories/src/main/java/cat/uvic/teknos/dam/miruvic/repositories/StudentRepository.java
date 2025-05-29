package cat.uvic.teknos.dam.miruvic.repositories;

import cat.uvic.teknos.dam.miruvic.model.Student;

public interface StudentRepository extends Repository<Integer, Student> {
    Student findByName(String name);

    Student findByEmail(String email);
    
    Student findByPhone(String phone_number);
}