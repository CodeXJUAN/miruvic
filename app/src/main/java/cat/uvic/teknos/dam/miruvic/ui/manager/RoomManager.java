package cat.uvic.teknos.dam.miruvic.ui.manager;

import cat.uvic.teknos.dam.miruvic.model.Room;
import cat.uvic.teknos.dam.miruvic.model.impl.RoomImpl;
import cat.uvic.teknos.dam.miruvic.repositories.RoomRepository;
import java.math.BigDecimal;
import java.util.Scanner;

public class RoomManager {
    private final RoomRepository repository;
    private final Scanner scanner;

    public RoomManager(RoomRepository repository, Scanner scanner) {
        this.repository = repository;
        this.scanner = scanner;
    }

    public void menu() {
        while (true) {
            System.out.println("--- Gestión de Habitaciones ---");
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
        Room room = new RoomImpl();
        System.out.print("Número de habitación: ");
        room.setRoomNumber(scanner.nextLine());
        System.out.print("Piso: ");
        room.setFloor(Integer.parseInt(scanner.nextLine()));
        System.out.print("Capacidad: ");
        room.setCapacity(Integer.parseInt(scanner.nextLine()));
        System.out.print("Tipo: ");
        room.setType(scanner.nextLine());
        System.out.print("Precio: ");
        room.setPrice(new BigDecimal(scanner.nextLine()));
        repository.save(room);
        System.out.println("Habitación creada.");
    }

    private void list() {
        for (Room room : repository.getAll()) {
            System.out.println("ID: " + room.getId() +
                    ", Número: " + room.getRoomNumber() +
                    ", Piso: " + room.getFloor() +
                    ", Capacidad: " + room.getCapacity() +
                    ", Tipo: " + room.getType() +
                    ", Precio: " + room.getPrice());
        }
    }

    private void update() {
        System.out.print("ID de la habitación a actualizar: ");
        Integer id = Integer.parseInt(scanner.nextLine());
        Room room = repository.get(id);
        if (room == null) {
            System.out.println("No encontrada.");
            return;
        }
        System.out.print("Nuevo número (" + room.getRoomNumber() + "): ");
        String number = scanner.nextLine();
        if (!number.isEmpty()) room.setRoomNumber(number);

        System.out.print("Nuevo piso (" + room.getFloor() + "): ");
        String floor = scanner.nextLine();
        if (!floor.isEmpty()) room.setFloor(Integer.parseInt(floor));

        System.out.print("Nueva capacidad (" + room.getCapacity() + "): ");
        String cap = scanner.nextLine();
        if (!cap.isEmpty()) room.setCapacity(Integer.parseInt(cap));

        System.out.print("Nuevo tipo (" + room.getType() + "): ");
        String type = scanner.nextLine();
        if (!type.isEmpty()) room.setType(type);

        System.out.print("Nuevo precio (" + room.getPrice() + "): ");
        String price = scanner.nextLine();
        if (!price.isEmpty()) room.setPrice(new BigDecimal(price));

        repository.save(room);
        System.out.println("Habitación actualizada.");
    }

    private void delete() {
        System.out.print("ID de la habitación a eliminar: ");
        Integer id = Integer.parseInt(scanner.nextLine());
        Room room = repository.get(id);
        if (room == null) {
            System.out.println("No encontrada.");
            return;
        }
        repository.delete(room);
        System.out.println("Habitación eliminada.");
    }
}