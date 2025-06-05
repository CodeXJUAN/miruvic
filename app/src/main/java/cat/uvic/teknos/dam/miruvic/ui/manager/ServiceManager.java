package cat.uvic.teknos.dam.miruvic.ui.manager;

import cat.uvic.teknos.dam.miruvic.model.Service;
import cat.uvic.teknos.dam.miruvic.model.impl.ServiceImpl;
import cat.uvic.teknos.dam.miruvic.repositories.ServiceRepository;
import java.math.BigDecimal;
import java.util.Scanner;

public class ServiceManager {
    private final ServiceRepository repository;
    private final Scanner scanner;

    public ServiceManager(ServiceRepository repository, Scanner scanner) {
        this.repository = repository;
        this.scanner = scanner;
    }

    public void menu() {
        while (true) {
            System.out.println("--- Gestión de Servicios ---");
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
        Service service = new ServiceImpl();
        System.out.print("Nombre del servicio: ");
        service.setServiceName(scanner.nextLine());
        System.out.print("Descripción: ");
        service.setDescription(scanner.nextLine());
        System.out.print("Precio: ");
        service.setPrice(new BigDecimal(scanner.nextLine()));
        repository.save(service);
        System.out.println("Servicio creado.");
    }

    private void list() {
        for (Service service : repository.getAll()) {
            System.out.println("ID: " + service.getId() +
                    ", Nombre: " + service.getServiceName() +
                    ", Descripción: " + service.getDescription() +
                    ", Precio: " + service.getPrice());
        }
    }

    private void update() {
        System.out.print("ID del servicio a actualizar: ");
        Integer id = Integer.parseInt(scanner.nextLine());
        Service service = repository.get(id);
        if (service == null) {
            System.out.println("No encontrado.");
            return;
        }
        System.out.print("Nuevo nombre (" + service.getServiceName() + "): ");
        String name = scanner.nextLine();
        if (!name.isEmpty()) service.setServiceName(name);

        System.out.print("Nueva descripción (" + service.getDescription() + "): ");
        String desc = scanner.nextLine();
        if (!desc.isEmpty()) service.setDescription(desc);

        System.out.print("Nuevo precio (" + service.getPrice() + "): ");
        String price = scanner.nextLine();
        if (!price.isEmpty()) service.setPrice(new BigDecimal(price));

        repository.save(service);
        System.out.println("Servicio actualizado.");
    }

    private void delete() {
        System.out.print("ID del servicio a eliminar: ");
        Integer id = Integer.parseInt(scanner.nextLine());
        Service service = repository.get(id);
        if (service == null) {
            System.out.println("No encontrado.");
            return;
        }
        repository.delete(service);
        System.out.println("Servicio eliminado.");
    }
}