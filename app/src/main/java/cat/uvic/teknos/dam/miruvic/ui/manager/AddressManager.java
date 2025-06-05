package cat.uvic.teknos.dam.miruvic.ui.manager;

import cat.uvic.teknos.dam.miruvic.model.Address;
import cat.uvic.teknos.dam.miruvic.model.impl.AddressImpl;
import cat.uvic.teknos.dam.miruvic.repositories.AddressRepository;
import java.util.Scanner;

public class AddressManager {
    private final AddressRepository repository;
    private final Scanner scanner;

    public AddressManager(AddressRepository repository, Scanner scanner) {
        this.repository = repository;
        this.scanner = scanner;
    }

    public void menu() {
        while (true) {
            System.out.println("--- Gestión de Direcciones ---");
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
        Address address = new AddressImpl();
        System.out.print("Calle: ");
        address.setStreet(scanner.nextLine());
        System.out.print("Ciudad: ");
        address.setCity(scanner.nextLine());
        System.out.print("Provincia: ");
        address.setState(scanner.nextLine());
        System.out.print("Código postal: ");
        address.setZipCode(scanner.nextLine());
        System.out.print("País: ");
        address.setCountry(scanner.nextLine());
        repository.save(address);
        System.out.println("Dirección creada.");
    }

    private void list() {
        for (Address address : repository.getAll()) {
            System.out.printf(
                "ID: %d, Calle: %s, Ciudad: %s, Provincia: %s, Código Postal: %s, País: %s%n",
                address.getId(),
                address.getStreet(),
                address.getCity(),
                address.getState(),
                address.getZipCode(),
                address.getCountry()
            );
        }
    }

    private void update() {
        System.out.print("ID de la dirección a actualizar: ");
        Integer id = Integer.parseInt(scanner.nextLine());
        Address address = repository.get(id);
        if (address == null) {
            System.out.println("No encontrada.");
            return;
        }
        System.out.print("Nueva calle (" + address.getStreet() + "): ");
        String street = scanner.nextLine();
        if (!street.isEmpty()) address.setStreet(street);

        System.out.print("Nueva ciudad (" + address.getCity() + "): ");
        String city = scanner.nextLine();
        if (!city.isEmpty()) address.setCity(city);

        System.out.print("Nueva provincia (" + address.getState() + "): ");
        String state = scanner.nextLine();
        if (!state.isEmpty()) address.setState(state);

        System.out.print("Nuevo código postal (" + address.getZipCode() + "): ");
        String postal = scanner.nextLine();
        if (!postal.isEmpty()) address.setZipCode(postal);

        System.out.print("Nuevo país (" + address.getCountry() + "): ");
        String country = scanner.nextLine();
        if (!country.isEmpty()) address.setCountry(country);

        repository.save(address);
        System.out.println("Dirección actualizada.");
    }

    private void delete() {
        System.out.print("ID de la dirección a eliminar: ");
        Integer id = Integer.parseInt(scanner.nextLine());
        Address address = repository.get(id);
        if (address == null) {
            System.out.println("No encontrada.");
            return;
        }
        repository.delete(address);
        System.out.println("Dirección eliminada.");
    }
}