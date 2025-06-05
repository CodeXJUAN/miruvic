package cat.uvic.teknos.dam.miruvic.ui.manager;

import cat.uvic.teknos.dam.miruvic.model.Reservation;
import cat.uvic.teknos.dam.miruvic.model.Student;
import cat.uvic.teknos.dam.miruvic.model.Room;
import cat.uvic.teknos.dam.miruvic.model.impl.ReservationImpl;
import cat.uvic.teknos.dam.miruvic.repositories.ReservationRepository;
import cat.uvic.teknos.dam.miruvic.repositories.StudentRepository;
import cat.uvic.teknos.dam.miruvic.repositories.RoomRepository;

import java.time.LocalDate;
import java.util.Scanner;

public class ReservationManager {
    private final ReservationRepository reservationRepository;
    private final StudentRepository studentRepository;
    private final RoomRepository roomRepository;
    private final Scanner scanner;

    public ReservationManager(ReservationRepository reservationRepository, StudentRepository studentRepository, RoomRepository roomRepository, Scanner scanner) {
        this.reservationRepository = reservationRepository;
        this.studentRepository = studentRepository;
        this.roomRepository = roomRepository;
        this.scanner = scanner;
    }

    public void menu() {
        while (true) {
            System.out.println("--- Gestión de Reservas ---");
            System.out.println("1. Crear");
            System.out.println("2. Listar");
            System.out.println("3. Actualizar");
            System.out.println("4. Eliminar");
            System.out.println("0. Volver");
            System.out.print("Opción: ");
            String option = scanner.nextLine();
            switch (option) {
                case "1": create(); break;
                case "2": list(); break;
                case "3": update(); break;
                case "4": delete(); break;
                case "0": return;
                default: System.out.println("Opción no válida.");
            }
        }
    }

    private void create() {
        Reservation reservation = new ReservationImpl();
        System.out.print("ID del estudiante: ");
        Integer studentId = Integer.parseInt(scanner.nextLine());
        Student student = studentRepository.get(studentId);
        if (student == null) {
            System.out.println("Estudiante no encontrado.");
            return;
        }
        reservation.setStudent(student);

        System.out.print("ID de la habitación: ");
        Integer roomId = Integer.parseInt(scanner.nextLine());
        Room room = roomRepository.get(roomId);
        if (room == null) {
            System.out.println("Habitación no encontrada.");
            return;
        }
        reservation.setRoom(room);

        System.out.print("Fecha de inicio (YYYY-MM-DD): ");
        reservation.setStartDate(LocalDate.parse(scanner.nextLine()));
        System.out.print("Fecha de fin (YYYY-MM-DD): ");
        reservation.setEndDate(LocalDate.parse(scanner.nextLine()));
        System.out.print("Estado: ");
        reservation.setStatus(scanner.nextLine());

        reservationRepository.save(reservation);
        System.out.println("Reserva creada.");
    }

    private void list() {
        for (Reservation reservation : reservationRepository.getAll()) {
            System.out.printf(
                    "ID: %d, Estudiante ID: %s, Habitación ID: %s, Inicio: %s, Fin: %s, Estado: %s%n",
                    reservation.getId(),
                    reservation.getStudent() != null ? reservation.getStudent().getId() : "N/A",
                    reservation.getRoom() != null ? reservation.getRoom().getId() : "N/A",
                    reservation.getStartDate(),
                    reservation.getEndDate(),
                    reservation.getStatus()
            );
        }
    }

    private void update() {
        System.out.print("ID de la reserva a actualizar: ");
        Integer id = Integer.parseInt(scanner.nextLine());
        Reservation reservation = reservationRepository.get(id);
        if (reservation == null) {
            System.out.println("No encontrada.");
            return;
        }

        System.out.print("Nuevo ID de estudiante (" + (reservation.getStudent() != null ? reservation.getStudent().getId() : "ninguno") + "): ");
        String studentIdStr = scanner.nextLine();
        if (!studentIdStr.isEmpty()) {
            Integer studentId = Integer.parseInt(studentIdStr);
            Student student = studentRepository.get(studentId);
            if (student != null) {
                reservation.setStudent(student);
            } else {
                System.out.println("Estudiante no encontrado. No se actualiza el estudiante.");
            }
        }

        System.out.print("Nuevo ID de habitación (" + (reservation.getRoom() != null ? reservation.getRoom().getId() : "ninguna") + "): ");
        String roomIdStr = scanner.nextLine();
        if (!roomIdStr.isEmpty()) {
            Integer roomId = Integer.parseInt(roomIdStr);
            Room room = roomRepository.get(roomId);
            if (room != null) {
                reservation.setRoom(room);
            } else {
                System.out.println("Habitación no encontrada. No se actualiza la habitación.");
            }
        }

        System.out.print("Nueva fecha de inicio (" + reservation.getStartDate() + "): ");
        String start = scanner.nextLine();
        if (!start.isEmpty()) reservation.setStartDate(LocalDate.parse(start));
        System.out.print("Nueva fecha de fin (" + reservation.getEndDate() + "): ");
        String end = scanner.nextLine();
        if (!end.isEmpty()) reservation.setEndDate(LocalDate.parse(end));
        System.out.print("Nuevo estado (" + reservation.getStatus() + "): ");
        String status = scanner.nextLine();
        if (!status.isEmpty()) reservation.setStatus(status);

        reservationRepository.save(reservation);
        System.out.println("Reserva actualizada.");
    }

    private void delete() {
        System.out.print("ID de la reserva a eliminar: ");
        Integer id = Integer.parseInt(scanner.nextLine());
        Reservation reservation = reservationRepository.get(id);
        if (reservation == null) {
            System.out.println("No encontrada.");
            return;
        }
        reservationRepository.delete(reservation);
        System.out.println("Reserva eliminada.");
    }
}