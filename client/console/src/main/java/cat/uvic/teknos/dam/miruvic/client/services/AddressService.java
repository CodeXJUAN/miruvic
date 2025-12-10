package cat.uvic.teknos.dam.miruvic.client.services;

import cat.uvic.teknos.dam.miruvic.client.ActivityAwareScanner;
import cat.uvic.teknos.dam.miruvic.client.models.AddressDTO;
import cat.uvic.teknos.dam.miruvic.utils.security.CryptoUtils;
import cat.uvic.teknos.dam.miruvic.utils.security.SecurityConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import rawhttp.core.RawHttp;
import rawhttp.core.RawHttpRequest;
import rawhttp.core.RawHttpResponse;
import rawhttp.core.body.StringBody;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class AddressService {

    private final String host;
    private final int port;
    private final ObjectMapper objectMapper;
    private final RawHttp rawHttp;
    private final CryptoUtils cryptoUtils;

    public AddressService(String host, int port, ObjectMapper objectMapper) {
        this.host = host;
        this.port = port;
        this.objectMapper = objectMapper;
        this.rawHttp = new RawHttp();
        this.cryptoUtils = new CryptoUtils();
    }

    public void listAll() {
        System.out.println("\n Listando todas las direcciones...");

        try (Socket socket = new Socket(host, port)) {

            RawHttpRequest request = rawHttp.parseRequest(
                    "GET /addresses HTTP/1.1\r\n" +
                            "Host: " + host + "\r\n" +
                            "Accept: application/json\r\n" +
                            "\r\n"
            );

            request.writeTo(socket.getOutputStream());

            RawHttpResponse<?> response = rawHttp.parseResponse(socket.getInputStream()).eagerly();
            validateResponseHash(response);

            if (response.getStatusCode() == 200 && response.getBody().isPresent()) {
                String json = response.getBody().get().decodeBodyToString(StandardCharsets.UTF_8);
                AddressDTO[] addresses = objectMapper.readValue(json, AddressDTO[].class);

                if (addresses.length == 0) {
                    System.out.println("No hay direcciones registradas.");
                } else {
                    System.out.println("Se encontraron " + addresses.length + " direcciones:\n");
                    printAddressTable(Arrays.asList(addresses));
                }
            } else {
                System.out.println("Error: " + response.getStatusCode() + " - " + response.getStartLine().getReason());
            }

        } catch (IOException e) {
            System.out.println("Error de conexion: " + e.getMessage());
        }
    }

    public void getById(ActivityAwareScanner scanner) {
        System.out.print("\n Ingrese el ID de la direccion: ");
        String id = scanner.nextLine().trim();

        try (Socket socket = new Socket(host, port)) {
            RawHttpRequest request = rawHttp.parseRequest(
                    "GET /addresses/" + id + " HTTP/1.1\r\n" +
                            "Host: " + host + "\r\n" +
                            "Accept: application/json\r\n" +
                            "\r\n"
            );

            request.writeTo(socket.getOutputStream());
            RawHttpResponse<?> response = rawHttp.parseResponse(socket.getInputStream()).eagerly();
            validateResponseHash(response);

            if (response.getStatusCode() == 200 && response.getBody().isPresent()) {
                String json = response.getBody().get().decodeBodyToString(StandardCharsets.UTF_8);
                AddressDTO address = objectMapper.readValue(json, AddressDTO.class);

                System.out.println("\n Direccion encontrada:");
                printAddressDetails(address);
            } else if (response.getStatusCode() == 404) {
                System.out.println(" No se encontro una direccion con ID " + id);
            } else {
                System.out.println(" Error: " + response.getStatusCode());
            }

        } catch (IOException e) {
            System.out.println(" Error de conexion: " + e.getMessage());
        }
    }

    public void create(ActivityAwareScanner scanner) {
        System.out.println("\n Crear nueva direccion");

        AddressDTO address = new AddressDTO();

        System.out.print("Calle: ");
        address.setStreet(scanner.nextLine().trim());

        System.out.print("Ciudad: ");
        address.setCity(scanner.nextLine().trim());

        System.out.print("Provincia/Estado: ");
        address.setState(scanner.nextLine().trim());

        System.out.print("Codigo Postal: ");
        address.setZipCode(scanner.nextLine().trim());

        System.out.print("Pais: ");
        address.setCountry(scanner.nextLine().trim());

        try (Socket socket = new Socket(host, port)) {
            String json = objectMapper.writeValueAsString(address);
            String hash = cryptoUtils.hash(json);

            RawHttpRequest request = rawHttp.parseRequest(
                    "POST /addresses HTTP/1.1\r\n" +
                            "Host: " + host + "\r\n" +
                            "Content-Type: application/json\r\n" +
                            SecurityConstants.HASH_HEADER + ": " + hash + "\r\n" +
                            "Content-Length: " + json.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                            "\r\n"
            ).withBody(new StringBody(json));

            request.writeTo(socket.getOutputStream());
            RawHttpResponse<?> response = rawHttp.parseResponse(socket.getInputStream()).eagerly();
            validateResponseHash(response);

            if (response.getStatusCode() == 201) {
                System.out.println(" Direccion creada exitosamente");
            } else {
                System.out.println(" Error al crear: " + response.getStatusCode());
            }

        } catch (IOException e) {
            System.out.println(" Error de conexion: " + e.getMessage());
        }
    }

    public void update(ActivityAwareScanner scanner) {
        System.out.print("\n Ingrese el ID de la direccion a actualizar: ");
        String id = scanner.nextLine().trim();

        AddressDTO address = new AddressDTO();

        System.out.print("Nueva calle: ");
        address.setStreet(scanner.nextLine().trim());

        System.out.print("Nueva ciudad: ");
        address.setCity(scanner.nextLine().trim());

        System.out.print("Nueva provincia/estado: ");
        address.setState(scanner.nextLine().trim());

        System.out.print("Nuevo codigo postal: ");
        address.setZipCode(scanner.nextLine().trim());

        System.out.print("Nuevo pais: ");
        address.setCountry(scanner.nextLine().trim());

        try (Socket socket = new Socket(host, port)) {
            String json = objectMapper.writeValueAsString(address);
            String hash = cryptoUtils.hash(json);

            RawHttpRequest request = rawHttp.parseRequest(
                    "PUT /addresses/" + id + " HTTP/1.1\r\n" +
                            "Host: " + host + "\r\n" +
                            "Content-Type: application/json\r\n" +
                            SecurityConstants.HASH_HEADER + ": " + hash + "\r\n" +
                            "Content-Length: " + json.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                            "\r\n"
            ).withBody(new StringBody(json));

            request.writeTo(socket.getOutputStream());
            RawHttpResponse<?> response = rawHttp.parseResponse(socket.getInputStream()).eagerly();
            validateResponseHash(response);

            if (response.getStatusCode() == 204) {
                System.out.println(" Direccion actualizada exitosamente");
            } else if (response.getStatusCode() == 404) {
                System.out.println(" No se encontro una direccion con ID " + id);
            } else {
                System.out.println(" Error al actualizar: " + response.getStatusCode());
            }

        } catch (IOException e) {
            System.out.println(" Error de conexion: " + e.getMessage());
        }
    }

    public void delete(ActivityAwareScanner scanner) {
        System.out.print("\n->  Ingrese el ID de la direccion a eliminar: ");
        String id = scanner.nextLine().trim();

        System.out.print("!! ¿Estas seguro? (s/n): ");
        String confirm = scanner.nextLine().trim().toLowerCase();

        if (!confirm.equals("s")) {
            System.out.println("Operacion cancelada.");
            return;
        }

        try (Socket socket = new Socket(host, port)) {
            RawHttpRequest request = rawHttp.parseRequest(
                    "DELETE /addresses/" + id + " HTTP/1.1\r\n" +
                            "Host: " + host + "\r\n" +
                            "\r\n"
            );

            request.writeTo(socket.getOutputStream());
            RawHttpResponse<?> response = rawHttp.parseResponse(socket.getInputStream()).eagerly();

            if (response.getStatusCode() == 204) {
                System.out.println(" Direccion eliminada exitosamente");
            } else {
                // Validar hash si hay body de error
                if (response.getBody().isPresent()) {
                    validateResponseHash(response);
                }
                
                if (response.getStatusCode() == 404) {
                    System.out.println(" No se encontro una direccion con ID " + id);
                } else {
                    System.out.println(" Error al eliminar: " + response.getStatusCode());
                }
            }

        } catch (IOException e) {
            System.out.println(" Error de conexion: " + e.getMessage());
        }
    }

    public void showMenu(ActivityAwareScanner scanner) {
        while (true) {
            System.out.println("\n MENU DE DIRECCIONES");
            System.out.println("1. Listar todas las direcciones");
            System.out.println("2. Buscar direccion por ID");
            System.out.println("3. Crear nueva direccion");
            System.out.println("4. Actualizar direccion");
            System.out.println("5. Eliminar direccion");
            System.out.println("0. Volver al menu principal");
            System.out.print("\n-> Seleccione una opcion: ");

            try {
                int option = Integer.parseInt(scanner.nextLine());

                switch (option) {
                    case 1:
                        listAll();
                        break;
                    case 2:
                        getById(scanner);
                        break;
                    case 3:
                        create(scanner);
                        break;
                    case 4:
                        update(scanner);
                        break;
                    case 5:
                        delete(scanner);
                        break;
                    case 0:
                        return;
                    default:
                        System.out.println("\n Opcion no valida");
                }
            } catch (NumberFormatException e) {
                System.out.println("\n Por favor, ingrese un numero valido");
            }
        }
    }

    private void printAddressTable(List<AddressDTO> addresses) {
        System.out.println("------------|-----------------|-----------------|------|");
        System.out.println("|    ID     |     CALLE     |   POBLACION    | C.P.    |");
        System.out.println("|-----------|---------------|----------------|---------|");

        for (AddressDTO address : addresses) {
            System.out.printf("| %-9s | %-12s | %-12s | %-7s |%n",
                    address.getId(),
                    truncateString(address.getStreet(), 12),
                    truncateString(address.getCity(), 12),
                    address.getZipCode());
        }

        System.out.println("|------------|-----------------|-----------------|------|");
    }

    private void printAddressDetails(AddressDTO address) {
        System.out.println("-------------------------------------------");
        System.out.println("│ DETALLES DE LA DIRECCION                |");
        System.out.println("|----------------|-------|-----------------|");
        System.out.printf("| ID              | %-20s  |%n", address.getId());
        System.out.printf("| Calle           | %-20s  |%n", address.getStreet());
        System.out.printf("| Ciudad          | %-20s  |%n", address.getCity());
        System.out.printf("| Codigo Postal   | %-20s  |%n", address.getZipCode());
        System.out.printf("| Estado          | %-20s  |%n", address.getState());
        System.out.printf("| Pais            | %-20s  |%n", address.getCountry());
        System.out.println("|----------------|-----------------|-----------------|------|");
    }

    private String truncateString(String str, int length) {
        if (str == null) return "";
        return str.length() > length ? str.substring(0, length - 3) + "..." : str;
    }

    private void validateResponseHash(RawHttpResponse<?> response) {
        String receivedHash = response.getHeaders()
                .getFirst(SecurityConstants.HASH_HEADER)
                .orElse(null);

        if (receivedHash == null) {
            throw new RuntimeException("Server response missing hash header");
        }

        try {
            String body = response.getBody().map(b -> {
                try {
                    return b.decodeBodyToString(StandardCharsets.UTF_8);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).orElse("");

            String computedHash = cryptoUtils.hash(body);

            if (!computedHash.equals(receivedHash)) {
                throw new RuntimeException("Response hash validation failed");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error decoding response body for hash validation", e);
        }
    }
}

