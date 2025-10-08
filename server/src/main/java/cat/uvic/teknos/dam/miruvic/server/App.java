package cat.uvic.teknos.dam.miruvic.server;

import cat.uvic.teknos.dam.miruvic.jdbc.repositories.JdbcRepositoryFactory;
import cat.uvic.teknos.dam.miruvic.repositories.AddressRepository;
import cat.uvic.teknos.dam.miruvic.repositories.StudentRepository;
import cat.uvic.teknos.dam.miruvic.server.controllers.AddressController;
import cat.uvic.teknos.dam.miruvic.server.controllers.StudentController;
import cat.uvic.teknos.dam.miruvic.server.routing.RequestRouter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;

public class App {

    public static void main(String[] args) {
        System.out.println("\n");
        System.out.println("███╗   ███╗██╗██████╗ ██╗   ██╗██╗   ██╗██╗ ██████╗");
        System.out.println("████╗ ████║██║██╔══██╗██║   ██║██║   ██║██║██╔════╝");
        System.out.println("██╔████╔██║██║██████╔╝██║   ██║██║   ██║██║██║     ");
        System.out.println("██║╚██╔╝██║██║██╔══██╗██║   ██║╚██╗ ██╔╝██║██║     ");
        System.out.println("██║ ╚═╝ ██║██║██║  ██║╚██████╔╝ ╚████╔╝ ██║╚██████╗");
        System.out.println("╚═╝     ╚═╝╚═╝╚═╝  ╚═╝ ╚═════╝   ╚═══╝  ╚═╝ ╚═════╝");
        System.out.println("                    SERVER v1.0");
        System.out.println();

        Server server = null;
        JdbcRepositoryFactory repositoryFactory = null;

        try {
            System.out.println("► Initializing database connection...");
            repositoryFactory = new JdbcRepositoryFactory();
            System.out.println("  ✓ Database connection established");

            AddressRepository addressRepository = repositoryFactory.getAddressRepository();
            StudentRepository studentRepository = repositoryFactory.getStudentRepository();
            System.out.println("  ✓ Repositories loaded");

            ObjectMapper objectMapper = new ObjectMapper();

            objectMapper.registerModule(new JavaTimeModule());
            System.out.println("  ✓ JSON mapper configured");

            AddressController addressController = new AddressController(addressRepository, objectMapper);
            StudentController studentController = new StudentController(studentRepository, addressRepository, objectMapper);
            System.out.println("  ✓ Controllers initialized (Address, Student)");

            RequestRouter router = new RequestRouter(addressController, studentController);
            System.out.println("  ✓ Request router configured");

            final int PORT = 5000;
            server = new Server(PORT, router);
            System.out.println("  ✓ Server instance created\n");

            final Server finalServer = server;
            final JdbcRepositoryFactory finalFactory = repositoryFactory;
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\n\n► Shutdown signal received...");
                if (finalServer != null) {
                    finalServer.shutdown();
                }
                if (finalFactory != null) {
                    try {
                        finalFactory.close();
                        System.out.println("  ✓ Database connections closed");
                    } catch (Exception e) {
                        System.err.println("  ✗ Error closing database: " + e.getMessage());
                    }
                }
                System.out.println("  ✓ Server shutdown complete\n");
            }));

            server.start();

        } catch (Exception e) {
            System.err.println("\n✗✗✗ FATAL ERROR ✗✗✗");
            System.err.println("Could not start server: " + e.getMessage());
            e.printStackTrace();

            if (repositoryFactory != null) {
                try {
                    repositoryFactory.close();
                } catch (Exception ex) {
                    System.err.println("Error closing resources: " + ex.getMessage());
                }
            }

            System.exit(1);
        }
    }
}