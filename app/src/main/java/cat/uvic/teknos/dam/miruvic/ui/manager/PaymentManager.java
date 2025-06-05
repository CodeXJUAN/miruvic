package cat.uvic.teknos.dam.miruvic.ui.manager;

import cat.uvic.teknos.dam.miruvic.model.Payment;
import cat.uvic.teknos.dam.miruvic.model.Reservation;
import cat.uvic.teknos.dam.miruvic.model.impl.PaymentImpl;
import cat.uvic.teknos.dam.miruvic.repositories.PaymentRepository;
import cat.uvic.teknos.dam.miruvic.repositories.ReservationRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Scanner;

public class PaymentManager {
    private final PaymentRepository repository;
    private final ReservationRepository reservationRepository;
    private final Scanner scanner;

    public PaymentManager(PaymentRepository repository, ReservationRepository reservationRepository, Scanner scanner) {
        this.repository = repository;
        this.reservationRepository = reservationRepository;
        this.scanner = scanner;
    }

    public void menu() {
        while (true) {
            System.out.println("--- Gestión de Pagos ---");
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
        Payment payment = new PaymentImpl();
        System.out.print("Importe: ");
        payment.setAmount(new BigDecimal(scanner.nextLine()));
        System.out.print("Fecha de pago (YYYY-MM-DD): ");
        payment.setPaymentDate(LocalDate.parse(scanner.nextLine()));
        System.out.print("Método de pago: ");
        payment.setPaymentMethod(scanner.nextLine());
        System.out.print("Estado: ");
        payment.setStatus(scanner.nextLine());
        System.out.print("ID de la reserva: ");
        Integer reservationId = Integer.parseInt(scanner.nextLine());
        Reservation reservation = reservationRepository.get(reservationId);
        if (reservation == null) {
            System.out.println("Reserva no encontrada. No se puede crear el pago.");
            return;
        }
        payment.setReservation(reservation);
        repository.save(payment);
        System.out.println("Pago creado.");
    }

    private void list() {
        for (Payment payment : repository.getAll()) {
            System.out.printf(
                "ID: %d, Importe: %.2f, Fecha: %s, Método: %s, Estado: %s, Reserva ID: %s",
                payment.getId(),
                payment.getAmount().doubleValue(),
                payment.getPaymentDate(),
                payment.getPaymentMethod(),
                payment.getStatus(),
                payment.getReservation() != null ? payment.getReservation().getId() : "N/A"
            );
        }
    }

    private void update() {
        System.out.print("ID del pago a actualizar: ");
        Integer id = Integer.parseInt(scanner.nextLine());
        Payment payment = repository.get(id);
        if (payment == null) {
            System.out.println("No encontrado.");
            return;
        }
        System.out.print("Nuevo importe (" + payment.getAmount() + "): ");
        String amount = scanner.nextLine();
        if (!amount.isEmpty()) payment.setAmount(new BigDecimal(amount));
        System.out.print("Nueva fecha (" + payment.getPaymentDate() + "): ");
        String date = scanner.nextLine();
        if (!date.isEmpty()) payment.setPaymentDate(LocalDate.parse(date));
        System.out.print("Nuevo método de pago (" + payment.getPaymentMethod() + "): ");
        String method = scanner.nextLine();
        if (!method.isEmpty()) payment.setPaymentMethod(method);
        System.out.print("Nuevo estado (" + payment.getStatus() + "): ");
        String status = scanner.nextLine();
        if (!status.isEmpty()) payment.setStatus(status);
        System.out.print("ID de la reserva (" + (payment.getReservation() != null ? payment.getReservation().getId() : "ninguna") + "): ");
        String reservationIdStr = scanner.nextLine();
        if (!reservationIdStr.isEmpty()) {
            Integer reservationId = Integer.parseInt(reservationIdStr);
            Reservation reservation = reservationRepository.get(reservationId);
            if (reservation != null) {
                payment.setReservation(reservation);
            } else {
                System.out.println("Reserva no encontrada. No se actualiza la reserva.");
            }
        }
        repository.save(payment);
        System.out.println("Pago actualizado.");
    }

    private void delete() {
        System.out.print("ID del pago a eliminar: ");
        Integer id = Integer.parseInt(scanner.nextLine());
        Payment payment = repository.get(id);
        if (payment == null) {
            System.out.println("No encontrado.");
            return;
        }
        repository.delete(payment);
        System.out.println("Pago eliminado.");
    }
}