package cat.uvic.teknos.dam.miruvic.ui.manager;

import cat.uvic.teknos.dam.miruvic.model.ReservationService;
import cat.uvic.teknos.dam.miruvic.model.Reservation;
import cat.uvic.teknos.dam.miruvic.model.Service;
import cat.uvic.teknos.dam.miruvic.model.impl.ReservationServiceImpl;
import cat.uvic.teknos.dam.miruvic.repositories.ReservationServiceRepository;
import cat.uvic.teknos.dam.miruvic.repositories.ReservationRepository;
import cat.uvic.teknos.dam.miruvic.repositories.ServiceRepository;

import java.util.Scanner;

public class ReservationServiceManager {
    private final ReservationServiceRepository repository;
    private final ReservationRepository reservationRepository;
    private final ServiceRepository serviceRepository;
    private final Scanner scanner;

    public ReservationServiceManager(
            ReservationServiceRepository repository,
            ReservationRepository reservationRepository,
            ServiceRepository serviceRepository,
            Scanner scanner
    ) {
        this.repository = repository;
        this.reservationRepository = reservationRepository;
        this.serviceRepository = serviceRepository;
        this.scanner = scanner;
    }

    public void menu() {
        while (true) {
            System.out.println("--- Gestión de Servicios de Reserva ---");
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
        ReservationService reservationService = new ReservationServiceImpl();

        System.out.print("ID de la reserva: ");
        Integer reservationId = Integer.parseInt(scanner.nextLine());
        Reservation reservation = reservationRepository.get(reservationId);
        if (reservation == null) {
            System.out.println("Reserva no encontrada.");
            return;
        }
        reservationService.setReservation(reservation);

        System.out.print("ID del servicio: ");
        Integer serviceId = Integer.parseInt(scanner.nextLine());
        Service service = serviceRepository.get(serviceId);
        if (service == null) {
            System.out.println("Servicio no encontrado.");
            return;
        }
        reservationService.setService(service);

        System.out.print("Cantidad: ");
        reservationService.setQuantity(Integer.parseInt(scanner.nextLine()));

        repository.save(reservationService);
        System.out.println("Servicio de reserva creado.");
    }

    private void list() {
        for (ReservationService rs : repository.getAll()) {
            System.out.println("Reserva ID: " + (rs.getReservation() != null ? rs.getReservation().getId() : "N/A") +
                    ", Servicio ID: " + (rs.getService() != null ? rs.getService().getId() : "N/A") +
                    ", Cantidad: " + rs.getQuantity());
        }
    }

    private void update() {
        System.out.print("ID de la reserva: ");
        Integer reservationId = Integer.parseInt(scanner.nextLine());
        System.out.print("ID del servicio: ");
        Integer serviceId = Integer.parseInt(scanner.nextLine());

        Reservation reservation = reservationRepository.get(reservationId);
        Service service = serviceRepository.get(serviceId);

        if (reservation == null || service == null) {
            System.out.println("Reserva o servicio no encontrado.");
            return;
        }

        ReservationService reservationService = null;
        for (ReservationService rs : repository.findByReservationId(reservationId)) {
            if (rs.getService() != null && rs.getService().getId().equals(serviceId)) {
                reservationService = rs;
                break;
            }
        }
        if (reservationService == null) {
            System.out.println("No existe esa relación reserva-servicio.");
            return;
        }

        System.out.print("Nueva cantidad (" + reservationService.getQuantity() + "): ");
        String quantityStr = scanner.nextLine();
        if (!quantityStr.isEmpty()) {
            reservationService.setQuantity(Integer.parseInt(quantityStr));
        }

        repository.save(reservationService);
        System.out.println("Servicio de reserva actualizado.");
    }

    private void delete() {
        System.out.print("ID de la reserva: ");
        Integer reservationId = Integer.parseInt(scanner.nextLine());
        System.out.print("ID del servicio: ");
        Integer serviceId = Integer.parseInt(scanner.nextLine());

        Reservation reservation = reservationRepository.get(reservationId);
        Service service = serviceRepository.get(serviceId);

        if (reservation == null || service == null) {
            System.out.println("Reserva o servicio no encontrado.");
            return;
        }

        ReservationService reservationService = null;
        for (ReservationService rs : repository.findByReservationId(reservationId)) {
            if (rs.getService() != null && rs.getService().getId().equals(serviceId)) {
                reservationService = rs;
                break;
            }
        }
        if (reservationService == null) {
            System.out.println("No existe esa relación reserva-servicio.");
            return;
        }

        repository.delete(reservationService);
        System.out.println("Servicio de reserva eliminado.");
    }
}