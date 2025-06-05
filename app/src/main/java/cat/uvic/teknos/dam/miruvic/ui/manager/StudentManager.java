package cat.uvic.teknos.dam.miruvic.ui.manager;

import cat.uvic.teknos.dam.miruvic.model.Student;
import cat.uvic.teknos.dam.miruvic.model.Address;
import cat.uvic.teknos.dam.miruvic.model.impl.StudentImpl;
import cat.uvic.teknos.dam.miruvic.repositories.StudentRepository;
import cat.uvic.teknos.dam.miruvic.repositories.AddressRepository;

import java.util.Scanner;

public class StudentManager {
    private final StudentRepository studentRepository;
    private final AddressRepository addressRepository;
    private final Scanner scanner;

    public StudentManager(StudentRepository studentRepository, AddressRepository addressRepository, Scanner scanner) {
        this.studentRepository = studentRepository;
        this.addressRepository = addressRepository;
        this.scanner = scanner;
    }

    public void menu() {
        while (true) {
            System.out.println("\n--- Gestión de Estudiantes ---");
            System.out.println("1. Crear estudiante");
            System.out.println("2. Listar estudiantes");
            System.out.println("3. Actualizar estudiante");
            System.out.println("4. Eliminar estudiante");
            System.out.println("0. Salir");
            System.out.print("Opción: ");
            String option = scanner.nextLine();
            switch (option) {
                case "1" -> create();
                case "2" -> list();
                case "3" -> update();
                case "4" -> delete();
                case "0" -> { return; }
                default -> System.out.println("Opción no válida.");
            }
        }
    }

    private void create() {
        Student student = new StudentImpl();
        System.out.print("Nombre: ");
        student.setFirstName(scanner.nextLine());
        System.out.print("Apellido: ");
        student.setLastName(scanner.nextLine());
        System.out.print("Email: ");
        student.setEmail(scanner.nextLine());
        System.out.print("Contraseña: ");
        student.setPasswordHash(scanner.nextLine());
        System.out.print("Teléfono: ");
        student.setPhoneNumber(scanner.nextLine());

        // Selección de dirección
        System.out.println("Direcciones disponibles:");
        addressRepository.getAll().forEach(address ->
                System.out.printf("ID: %d, %s, %s, %s, %s, %s%n",
                        address.getId(), address.getStreet(), address.getCity(),
                        address.getState(), address.getZipCode(), address.getCountry())
        );
        System.out.print("ID de la dirección (dejar vacío para ninguna): ");
        String addressIdStr = scanner.nextLine();
        if (!addressIdStr.isEmpty()) {
            Address address = addressRepository.get(Integer.parseInt(addressIdStr));
            if (address != null) {
                student.setAddress(address);
            }
        }

        studentRepository.save(student);
        System.out.println("Estudiante creado.");
    }

    private void list() {
        for (Student student : studentRepository.getAll()) {
            System.out.printf(
                    "ID: %d, Nombre: %s %s, Email: %s, Contraseña: %s, Teléfono: %s, ID Dirección: %s%n",
                    student.getId(),
                    student.getFirstName(),
                    student.getLastName(),
                    student.getEmail(),
                    student.getPasswordHash(),
                    student.getPhoneNumber(),
                    student.getAddress() != null ? student.getAddress().getId() : "N/A"
            );
        }
    }

    private void update() {
        System.out.print("ID del estudiante a actualizar: ");
        Integer id = Integer.parseInt(scanner.nextLine());
        Student student = studentRepository.get(id);
        if (student == null) {
            System.out.println("No encontrado.");
            return;
        }
        System.out.print("Nuevo nombre (" + student.getFirstName() + "): ");
        String firstName = scanner.nextLine();
        if (!firstName.isEmpty()) student.setFirstName(firstName);

        System.out.print("Nuevo apellido (" + student.getLastName() + "): ");
        String lastName = scanner.nextLine();
        if (!lastName.isEmpty()) student.setLastName(lastName);

        System.out.print("Nuevo email (" + student.getEmail() + "): ");
        String email = scanner.nextLine();
        if (!email.isEmpty()) student.setEmail(email);

        System.out.print("Nueva contraseña: ");
        String password = scanner.nextLine();
        if (!password.isEmpty()) student.setPasswordHash(password);

        System.out.print("Nuevo teléfono (" + student.getPhoneNumber() + "): ");
        String phone = scanner.nextLine();
        if (!phone.isEmpty()) student.setPhoneNumber(phone);

        // Actualizar dirección
        System.out.println("Direcciones disponibles:");
        addressRepository.getAll().forEach(address ->
                System.out.printf("ID: %d, %s, %s, %s, %s, %s%n",
                        address.getId(), address.getStreet(), address.getCity(),
                        address.getState(), address.getZipCode(), address.getCountry())
        );
        System.out.print("ID de la nueva dirección (dejar vacío para no cambiar): ");
        String addressIdStr = scanner.nextLine();
        if (!addressIdStr.isEmpty()) {
            Address address = addressRepository.get(Integer.parseInt(addressIdStr));
            if (address != null) {
                student.setAddress(address);
            }
        }

        studentRepository.save(student);
        System.out.println("Estudiante actualizado.");
    }

    private void delete() {
        System.out.print("ID del estudiante a eliminar: ");
        Integer id = Integer.parseInt(scanner.nextLine());
        Student student = studentRepository.get(id);
        if (student == null) {
            System.out.println("No encontrado.");
            return;
        }
        studentRepository.delete(student);
        System.out.println("Estudiante eliminado.");
    }
}