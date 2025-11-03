package cat.uvic.teknos.dam.miruvic.server;

import cat.uvic.teknos.dam.miruvic.jdbc.repositories.JdbcRepositoryFactory;
import cat.uvic.teknos.dam.miruvic.repositories.AddressRepository;
import cat.uvic.teknos.dam.miruvic.repositories.StudentRepository;
import cat.uvic.teknos.dam.miruvic.server.controllers.AddressController;
import cat.uvic.teknos.dam.miruvic.server.controllers.StudentController;
import cat.uvic.teknos.dam.miruvic.server.factories.DefaultModelFactory;
import cat.uvic.teknos.dam.miruvic.server.factories.ModelFactory;
import cat.uvic.teknos.dam.miruvic.server.routing.RequestRouter;
import cat.uvic.teknos.dam.miruvic.server.utils.HttpResponseBuilder;
import cat.uvic.teknos.dam.miruvic.server.utils.JsonRequestParser;
import cat.uvic.teknos.dam.miruvic.server.utils.PathParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class App {

    public static void main(String[] args) {
        System.out.println("\n");
        System.out.println("--------------------------------");
        System.out.println("   MIRUVIC APPLICATION SERVER   ");
        System.out.println("--------------------------------");
        System.out.println("Juan Manuel Lopez    SERVER v1.0");
        System.out.println("--------------------------------\n");

        Server server = null;
        JdbcRepositoryFactory repositoryFactory = null;

        try {
            System.out.println("> Initializing database connection...");
            repositoryFactory = new JdbcRepositoryFactory();
            System.out.println("v Database connection established");

            AddressRepository addressRepository = repositoryFactory.getAddressRepository();
            StudentRepository studentRepository = repositoryFactory.getStudentRepository();
            System.out.println("v Repositories loaded");

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            System.out.println("v JSON mapper configured");

            ModelFactory modelFactory = new DefaultModelFactory();
            JsonRequestParser jsonParser = new JsonRequestParser(objectMapper);
            HttpResponseBuilder responseBuilder = new HttpResponseBuilder();
            PathParser pathParser = new PathParser();
            System.out.println("v Utility classes initialized");

            AddressController addressController = new AddressController(
                    addressRepository,
                    modelFactory,
                    jsonParser,
                    responseBuilder,
                    pathParser
            );

            StudentController studentController = new StudentController(
                    studentRepository,
                    addressRepository,
                    modelFactory,
                    jsonParser,
                    responseBuilder,
                    pathParser
            );
            System.out.println("v Controllers initialized (Address, Student)");

            RequestRouter router = new RequestRouter(
                    addressController,
                    studentController,
                    responseBuilder,
                    pathParser
            );
            System.out.println("v Request router configured");

            final int PORT = 5000;
            server = new Server(PORT, router);
            System.out.println("v Server instance created\n");

            final Server finalServer = server;
            final JdbcRepositoryFactory finalFactory = repositoryFactory;
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\n\n > Shutdown signal received...");
                if (finalServer != null) {
                    finalServer.shutdown();
                }
                if (finalFactory != null) {
                    try {
                        finalFactory.close();
                        System.out.println("v Database connections closed");
                    } catch (Exception e) {
                        System.err.println("x Error closing database: " + e.getMessage());
                    }
                }
                System.out.println("v Server shutdown complete\n");
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