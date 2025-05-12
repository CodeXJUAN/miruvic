package cat.uvic.teknos.dam.ruvic.jdbc;

import cat.uvic.teknos.dam.ruvic.jdbc.classes.*;
import cat.uvic.teknos.dam.ruvic.jdbc.repo.*;

public class App {
    public static void main(String[] args) {
        StudentRepository studentRepo = new StudentRepository();
        AddressRepository addressRepo = new AddressRepository();

        // 1. Crear la dirección primero
        Address address = new Address();
        address.setStreet("Carrer Pere III");
        address.setCity("Santpedor");
        address.setState("Barcelona");
        address.setZipCode("08251");
        address.setCountry("Spain");
        addressRepo.save(address); // Guardar primero para obtener ID

        // 2. Crear el estudiante con la dirección ya existente
        Student student = new Student();
        student.setFirstName("Chema");
        student.setLastName("Alvarez");
        student.setEmail("chema.alvarez@example.com");
        student.setPasswordHash("123password");
        student.setPhoneNumber("698765432");
        student.setAddress(address); // Asignar dirección existente

        // 3. Guardar el estudiante
        studentRepo.save(student);

        System.out.println("Estudiante creado con ID: " + student.getId());
        System.out.println("Dirección creada con ID: " + address.getId());
    }
}