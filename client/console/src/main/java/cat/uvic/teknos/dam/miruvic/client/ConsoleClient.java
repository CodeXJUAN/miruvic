package cat.uvic.teknos.dam.miruvic.client;

import cat.uvic.teknos.dam.miruvic.client.services.AddressService;
import cat.uvic.teknos.dam.miruvic.client.services.StudentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import rawhttp.core.RawHttp;
import rawhttp.core.RawHttpRequest;
import rawhttp.core.RawHttpResponse;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConsoleClient {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 5000;
    private static final long INACTIVITY_TIMEOUT_MS = 120_000;

    private final ActivityAwareScanner scanner;
    private final AddressService addressService;
    private final StudentService studentService;
    private final RawHttp rawHttp = new RawHttp();

    // Inactivity tracking
    private final Lock activityLock;
    private volatile long lastActivityTime;
    private final ScheduledExecutorService inactivityMonitor;

    public ConsoleClient() {
        // Inactivity setup
        this.activityLock = new ReentrantLock();
        this.inactivityMonitor = Executors.newScheduledThreadPool(1);
        updateActivity(); // Initial activity

        // Scanner con detección de actividad
        this.scanner = new ActivityAwareScanner(new Scanner(System.in), this::updateActivity);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        this.addressService = new AddressService(SERVER_HOST, SERVER_PORT, objectMapper);
        this.studentService = new StudentService(SERVER_HOST, SERVER_PORT, objectMapper);

        startInactivityMonitor();
    }

    public static void main(String[] args) {
        ConsoleClient client = new ConsoleClient();
        client.run();
    }

    public void updateActivity() {
        activityLock.lock();
        try {
            lastActivityTime = System.currentTimeMillis();
        } finally {
            activityLock.unlock();
        }
    }

    private void startInactivityMonitor() {
        inactivityMonitor.scheduleAtFixedRate(() -> {
            activityLock.lock();
            try {
                long inactiveTime = System.currentTimeMillis() - lastActivityTime;
                if (inactiveTime >= INACTIVITY_TIMEOUT_MS) {
                    System.out.println("\n⚠️ Inactivity detected. Disconnecting...");
                    sendDisconnectAndExit();
                }
            } finally {
                activityLock.unlock();
            }
        }, 10, 10, TimeUnit.SECONDS);
    }

    private void sendDisconnectAndExit() {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT)) {
            System.out.println("  → Sending disconnect message to server...");
            RawHttpRequest disconnectRequest = rawHttp.parseRequest("GET /disconnect HTTP/1.1\nHost: " + SERVER_HOST);
            disconnectRequest.writeTo(socket.getOutputStream());

            RawHttpResponse<?> response = rawHttp.parseResponse(socket.getInputStream()).eagerly();

            if (response.getStatusCode() == 200) {
                System.out.println("  ✓ Server acknowledged disconnect.");
            } else {
                System.out.println("  ✗ Server responded with status: " + response.getStatusCode());
            }
        } catch (IOException e) {
            System.err.println("  ✗ Error during disconnect: " + e.getMessage());
        } finally {
            System.out.println("Exiting client.");
            inactivityMonitor.shutdownNow();
            System.exit(0);
        }
    }

    public void run() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║     MIRUVIC CLIENT v1.0               ║");
        System.out.println("╚════════════════════════════════════════╝");

        while (true) {
            try {
                showMenu();
                int option = readOption();

                switch (option) {
                    case 1:
                        manageAddresses();
                        break;
                    case 2:
                        manageStudents();
                        break;
                    case 0:
                        System.out.println("\n👋 ¡Hasta pronto!");
                        sendDisconnectAndExit();
                        return;
                    default:
                        System.out.println("\n❌ Opción no válida");
                }
            } catch (Exception e) {
                System.err.println("\n❌ Error: " + e.getMessage());
            }
        }
    }

    private void showMenu() {
        System.out.println("\n📋 MENÚ PRINCIPAL");
        System.out.println("1. Gestionar direcciones");
        System.out.println("2. Gestionar estudiantes");
        System.out.println("0. Salir");
        System.out.print("\n→ Seleccione una opción: ");
    }

    private int readOption() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void manageAddresses() {
        System.out.println("\n📍 GESTIÓN DE DIRECCIONES");
        addressService.showMenu(scanner);
    }

    private void manageStudents() {
        System.out.println("\n👥 GESTIÓN DE ESTUDIANTES");
        studentService.showMenu(scanner);
    }
}