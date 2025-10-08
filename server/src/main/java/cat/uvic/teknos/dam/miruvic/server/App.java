package cat.uvic.teknos.dam.miruvic.server;

import cat.uvic.teknos.dam.miruvic.jdbc.repositories.JdbcRepositoryFactory;
import cat.uvic.teknos.dam.miruvic.server.controllers.AddressController;
import cat.uvic.teknos.dam.miruvic.server.routing.RequestRouter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class App {
    public static void main(String[] args) throws IOException {
        var properties = new Properties();
        try (var fis = new FileInputStream("server.properties")) {
            properties.load(fis);
        }

        try (var repositoryFactory = new JdbcRepositoryFactory()) {
            var addressRepository = repositoryFactory.getAddressRepository();

            var objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            var addressController = new AddressController(addressRepository, objectMapper);
            var router = new RequestRouter(addressController, objectMapper);

            var server = new Server(5000, router);
            server.start();
        }
    }
}