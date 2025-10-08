package cat.uvic.teknos.dam.miruvic.client;

import cat.uvic.teknos.dam.miruvic.client.services.AddressService;
import cat.uvic.teknos.dam.miruvic.client.services.StudentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.Scanner;

/**
 * Cliente de consola para interactuar con el servidor MIRUVIC.
 * Responsabilidad: Mostrar menú y coordinar las operaciones del usuario.
 */
public class ConsoleClient {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 5000;

    private final Scanner scanner;
    private final AddressService addressService;
    private final StudentService studentService;

    public ConsoleClient() {
        this.scanner = new Scanner(System.in);

        // Configurar ObjectMapper con soporte para fechas
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Crear servicios
        this.addressService = new AddressService(SERVER_HOST, SERVER_PORT, objectMapper);
        this.studentService = new StudentService(SERVER_HOST, SERVER_PORT, objectMapper);
    }

    public static void main(String[] args) {
        ConsoleClient client = new ConsoleClient();
        client.run();
    }

    public void run() {
        showBanner();

        System.out.println("Conectando al servidor " + SERVER_HOST + ":" + SERVER_PORT + "...");
        System.out.println("✓ Cliente iniciado correctamente\n");

        boolean running = true;
        while (running) {
            showMainMenu();
            String option = scanner.nextLine().trim();

            switch (option) {
                case "1":
                    manageAddresses();
                    break;
                case "2":
                    manageStudents();
                    break;
                case "0":
                    running = false;
                    System.out.println("\n¡Hasta pronto! 👋");
                    break;
                default:
                    System.out.println("❌ Opción no válida. Intenta de nuevo.\n");
            }
        }

        scanner.close();
    }

    private void showBanner() {
        System.out.println("\n");
        System.out.println("███╗   ███╗██╗██████╗ ██╗   ██╗██╗   ██╗██╗ ██████╗");
        System.out.println("████╗ ████║██║██╔══██╗██║   ██║██║   ██║██║██╔════╝");
        System.out.println("██╔████╔██║██║██████╔╝██║   ██║██║   ██║██║██║     ");
        System.out.println("██║╚██╔╝██║██║██╔══██╗██║   ██║╚██╗ ██╔╝██║██║     ");
        System.out.println("██║ ╚═╝ ██║██║██║  ██║╚██████╔╝ ╚████╔╝ ██║╚██████╗");
        System.out.println("╚═╝     ╚═╝╚═╝╚═╝  ╚═╝ ╚═════╝   ╚═══╝  ╚═╝ ╚═════╝");
        System.out.println("                   CLIENT v1.0");
        System.out.println();
    }

    private void showMainMenu() {
        System.out.println("╔════════════════════════════════════╗");
        System.out.println("║         MENÚ PRINCIPAL            ║");
        System.out.println("╠════════════════════════════════════╣");
        System.out.println("║  1. Gestionar Direcciones         ║");
        System.out.println("║  2. Gestionar Estudiantes         ║");
        System.out.println("║  0. Salir                         ║");
        System.out.println("╚════════════════════════════════════╝");
        System.out.print("Seleccione una opción: ");
    }

    private void manageAddresses() {
        boolean back = false;
        while (!back) {
            System.out.println("\n╔════════════════════════════════════╗");
            System.out.println("║     GESTIÓN DE DIRECCIONES        ║");
            System.out.println("╠════════════════════════════════════╣");
            System.out.println("║  1. Ver todas las direcciones     ║");
            System.out.println("║  2. Ver dirección por ID          ║");
            System.out.println("║  3. Crear nueva dirección         ║");
            System.out.println("║  4. Actualizar dirección          ║");
            System.out.println("║  5. Eliminar dirección            ║");
            System.out.println("║  0. Volver                        ║");
            System.out.println("╚════════════════════════════════════╝");
            System.out.print("Seleccione una opción: ");

            String option = scanner.nextLine().trim();

            switch (option) {
                case "1":
                    addressService.listAll();
                    break;
                case "2":
                    addressService.getById(scanner);
                    break;
                case "3":
                    addressService.create(scanner);
                    break;
                case "4":
                    addressService.update(scanner);
                    break;
                case "5":
                    addressService.delete(scanner);
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println("❌ Opción no válida.\n");
            }
        }
    }

    private void manageStudents() {
        boolean back = false;
        while (!back) {
            System.out.println("\n╔════════════════════════════════════╗");
            System.out.println("║     GESTIÓN DE ESTUDIANTES        ║");
            System.out.println("╠════════════════════════════════════╣");
            System.out.println("║  1. Ver todos los estudiantes     ║");
            System.out.println("║  2. Ver estudiante por ID         ║");
            System.out.println("║  3. Crear nuevo estudiante        ║");
            System.out.println("║  4. Actualizar estudiante         ║");
            System.out.println("║  5. Eliminar estudiante           ║");
            System.out.println("║  0. Volver                        ║");
            System.out.println("╚════════════════════════════════════╝");
            System.out.print("Seleccione una opción: ");

            String option = scanner.nextLine().trim();

            switch (option) {
                case "1":
                    studentService.listAll();
                    break;
                case "2":
                    studentService.getById(scanner);
                    break;
                case "3":
                    studentService.create(scanner);
                    break;
                case "4":
                    studentService.update(scanner);
                    break;
                case "5":
                    studentService.delete(scanner);
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println("❌ Opción no válida.\n");
            }
        }
    }
}