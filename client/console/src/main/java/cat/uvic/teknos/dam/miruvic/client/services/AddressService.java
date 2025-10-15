package cat.uvic.teknos.dam.miruvic.client.services;

import cat.uvic.teknos.dam.miruvic.client.models.AddressDTO;
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
import java.util.Scanner;

public class AddressService {

    private final String host;
    private final int port;
    private final ObjectMapper objectMapper;
    private final RawHttp rawHttp;

    public AddressService(String host, int port, ObjectMapper objectMapper) {
        this.host = host;
        this.port = port;
        this.objectMapper = objectMapper;
        this.rawHttp = new RawHttp();
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

            if (response.getStatusCode() == 200) {
                String json = response.getBody().toString();
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
            System.out.println("Error de conexión: " + e.getMessage());
        }
    }

    public void getById(Scanner scanner) {
        System.out.print("\n Ingrese el ID de la dirección: ");
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

            if (response.getStatusCode() == 200) {
                String json = response.getBody().toString();
                AddressDTO address = objectMapper.readValue(json, AddressDTO.class);

                System.out.println("\n Dirección encontrada:");
                printAddressDetails(address);
            } else if (response.getStatusCode() == 404) {
                System.out.println(" No se encontró una dirección con ID " + id);
            } else {
                System.out.println(" Error: " + response.getStatusCode());
            }

        } catch (IOException e) {
            System.out.println(" Error de conexión: " + e.getMessage());
        }
    }

    public void create(Scanner scanner) {
        System.out.println("\n Crear nueva dirección");

        AddressDTO address = new AddressDTO();

        System.out.print("Calle: ");
        address.setStreet(scanner.nextLine().trim());

        System.out.print("Ciudad: ");
        address.setCity(scanner.nextLine().trim());

        System.out.print("Provincia/Estado: ");
        address.setState(scanner.nextLine().trim());

        System.out.print("Código Postal: ");
        address.setZipCode(scanner.nextLine().trim());

        System.out.print("País: ");
        address.setCountry(scanner.nextLine().trim());

        try (Socket socket = new Socket(host, port)) {
            String json = objectMapper.writeValueAsString(address);

            RawHttpRequest request = rawHttp.parseRequest(
                    "POST /addresses HTTP/1.1\r\n" +
                            "Host: " + host + "\r\n" +
                            "Content-Type: application/json\r\n" +
                            "Content-Length: " + json.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                            "\r\n"
            ).withBody(new StringBody(json));

            request.writeTo(socket.getOutputStream());
            RawHttpResponse<?> response = rawHttp.parseResponse(socket.getInputStream()).eagerly();

            if (response.getStatusCode() == 201) {
                System.out.println(" Dirección creada exitosamente");
            } else {
                System.out.println(" Error al crear: " + response.getStatusCode());
            }

        } catch (IOException e) {
            System.out.println(" Error de conexión: " + e.getMessage());
        }
    }

    public void update(Scanner scanner) {
        System.out.print("\n Ingrese el ID de la dirección a actualizar: ");
        String id = scanner.nextLine().trim();

        AddressDTO address = new AddressDTO();

        System.out.print("Nueva calle: ");
        address.setStreet(scanner.nextLine().trim());

        System.out.print("Nueva ciudad: ");
        address.setCity(scanner.nextLine().trim());

        System.out.print("Nueva provincia/estado: ");
        address.setState(scanner.nextLine().trim());

        System.out.print("Nuevo código postal: ");
        address.setZipCode(scanner.nextLine().trim());

        System.out.print("Nuevo país: ");
        address.setCountry(scanner.nextLine().trim());

        try (Socket socket = new Socket(host, port)) {
            String json = objectMapper.writeValueAsString(address);

            RawHttpRequest request = rawHttp.parseRequest(
                    "PUT /addresses/" + id + " HTTP/1.1\r\n" +
                            "Host: " + host + "\r\n" +
                            "Content-Type: application/json\r\n" +
                            "Content-Length: " + json.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                            "\r\n"
            ).withBody(new StringBody(json));

            request.writeTo(socket.getOutputStream());
            RawHttpResponse<?> response = rawHttp.parseResponse(socket.getInputStream()).eagerly();

            if (response.getStatusCode() == 204) {
                System.out.println(" Dirección actualizada exitosamente");
            } else if (response.getStatusCode() == 404) {
                System.out.println(" No se encontró una dirección con ID " + id);
            } else {
                System.out.println(" Error al actualizar: " + response.getStatusCode());
            }

        } catch (IOException e) {
            System.out.println(" Error de conexión: " + e.getMessage());
        }
    }

    public void delete(Scanner scanner) {
        System.out.print("\n🗑  Ingrese el ID de la dirección a eliminar: ");
        String id = scanner.nextLine().trim();

        System.out.print("⚠ ¿Está seguro? (s/n): ");
        String confirm = scanner.nextLine().trim().toLowerCase();

        if (!confirm.equals("s")) {
            System.out.println("Operación cancelada.");
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
                System.out.println(" Dirección eliminada exitosamente");
            } else if (response.getStatusCode() == 404) {
                System.out.println(" No se encontró una dirección con ID " + id);
            } else {
                System.out.println(" Error al eliminar: " + response.getStatusCode());
            }

        } catch (IOException e) {
            System.out.println(" Error de conexión: " + e.getMessage());
        }
    }

    private void printAddressTable(List<AddressDTO> addresses) {
        System.out.println("┌──────┬─────────────────────┬─────────────────┬─────────────────┬────────────┬─────────────┐");
        System.out.println("│  ID  │       Calle         │     Ciudad      │   Provincia     │   Código   │    País     │");
        System.out.println("├──────┼─────────────────────┼─────────────────┼─────────────────┼────────────┼─────────────┤");

        for (AddressDTO addr : addresses) {
            System.out.printf("│ %-4d │ %-19s │ %-15s │ %-15s │ %-10s │ %-11s │%n",
                    addr.getId() != null ? addr.getId() : 0,
                    truncate(addr.getStreet(), 19),
                    truncate(addr.getCity(), 15),
                    truncate(addr.getState(), 15),
                    truncate(addr.getZipCode(), 10),
                    truncate(addr.getCountry(), 11)
            );
        }

        System.out.println("└──────┴─────────────────────┴─────────────────┴─────────────────┴────────────┴─────────────┘");
    }

    private void printAddressDetails(AddressDTO address) {
        System.out.println("┌────────────────────────────────────────┐");
        System.out.println("│        DETALLES DE LA DIRECCIÓN       │");
        System.out.println("├────────────────────────────────────────┤");
        System.out.println("│ ID:         " + address.getId());
        System.out.println("│ Calle:      " + address.getStreet());
        System.out.println("│ Ciudad:     " + address.getCity());
        System.out.println("│ Provincia:  " + address.getState());
        System.out.println("│ CP:         " + address.getZipCode());
        System.out.println("│ País:       " + address.getCountry());
        System.out.println("└────────────────────────────────────────┘");
    }

    private String truncate(String str, int length) {
        if (str == null) return "";
        return str.length() > length ? str.substring(0, length - 3) + "..." : str;
    }
}