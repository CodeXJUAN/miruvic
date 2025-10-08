package cat.uvic.teknos.dam.miruvic.client;

import cat.uvic.teknos.dam.miruvic.client.exceptions.ClientException;
import cat.uvic.teknos.dam.miruvic.client.models.AddressImpl;
import cat.uvic.teknos.dam.miruvic.model.Address;
import com.fasterxml.jackson.databind.ObjectMapper;
import rawhttp.core.RawHttp;
import rawhttp.core.RawHttpRequest;
import rawhttp.core.RawHttpResponse;
import rawhttp.core.body.StringBody;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client {
    private static final RawHttp rawHttp = new RawHttp();
    private static final String HOST = "localhost";
    private static final int PORT = 5000;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {
        try (var scanner = new Scanner(System.in)) {
            while (true) {
                printMenu();
                var option = scanner.nextLine();
                switch (option) {
                    case "1":
                        getAddressById(scanner);
                        break;
                    case "2":
                        createAddress(scanner);
                        break;
                    case "3":
                        updateAddress(scanner);
                        break;
                    case "4":
                        deleteAddress(scanner);
                        break;
                    case "0":
                        return;
                    default:
                        System.out.println("Invalid option.");
                }
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n--- Address Management ---");
        System.out.println("1. Get address by ID");
        System.out.println("2. Create address");
        System.out.println("3. Update address");
        System.out.println("4. Delete address");
        System.out.println("0. Exit");
        System.out.print("Option: ");
    }

    private static void getAddressById(Scanner scanner) {
        System.out.print("Enter address ID: ");
        var id = scanner.nextLine();
        var request = rawHttp.parseRequest(
                "GET /addresses/" + id + " HTTP/1.1\r\n" +
                        "Host: " + HOST + "\r\n" +
                        "Accept: application/json\r\n"
        );
        try (var socket = new Socket(HOST, PORT)) {
            request.writeTo(socket.getOutputStream());
            var response = rawHttp.parseResponse(socket.getInputStream()).eagerly();
            if (response.getStatusCode() == 200) {
                var address = objectMapper.readValue(response.getBody().get().decodeBodyToString(StandardCharsets.UTF_8), AddressImpl.class);
                printAddress(address);
            } else {
                printResponseInfo(response);
            }
        } catch (IOException e) {
            throw new ClientException(e);
        }
    }

    private static void createAddress(Scanner scanner) {
        try (var socket = new Socket(HOST, PORT)) {
            var address = new AddressImpl();
            System.out.print("Street: ");
            address.setStreet(scanner.nextLine());
            System.out.print("City: ");
            address.setCity(scanner.nextLine());
            System.out.print("State: ");
            address.setState(scanner.nextLine());
            System.out.print("Zip Code: ");
            address.setZipCode(scanner.nextLine());
            System.out.print("Country: ");
            address.setCountry(scanner.nextLine());

            var json = objectMapper.writeValueAsString(address);
            var request = rawHttp.parseRequest(
                    "POST /addresses HTTP/1.1\r\n" +
                            "Host: " + HOST + "\r\n" +
                            "Content-Type: application/json\r\n" +
                            "Content-Length: " + json.length() + "\r\n"
            ).withBody(new StringBody(json));

            request.writeTo(socket.getOutputStream());
            var response = rawHttp.parseResponse(socket.getInputStream()).eagerly();
            printResponseInfo(response);

        } catch (IOException e) {
            throw new ClientException(e);
        }
    }

    private static void updateAddress(Scanner scanner) {
        System.out.print("Enter address ID to update: ");
        var id = scanner.nextLine();

        try (var socket = new Socket(HOST, PORT)) {
            var address = new AddressImpl();
            System.out.print("New Street: ");
            address.setStreet(scanner.nextLine());
            System.out.print("New City: ");
            address.setCity(scanner.nextLine());
            System.out.print("New State: ");
            address.setState(scanner.nextLine());
            System.out.print("New Zip Code: ");
            address.setZipCode(scanner.nextLine());
            System.out.print("New Country: ");
            address.setCountry(scanner.nextLine());

            var json = objectMapper.writeValueAsString(address);
            var request = rawHttp.parseRequest(
                    "PUT /addresses/" + id + " HTTP/1.1\r\n" +
                            "Host: " + HOST + "\r\n" +
                            "Content-Type: application/json\r\n" +
                            "Content-Length: " + json.length() + "\r\n"
            ).withBody(new StringBody(json));

            request.writeTo(socket.getOutputStream());
            var response = rawHttp.parseResponse(socket.getInputStream()).eagerly();
            printResponseInfo(response);

        } catch (IOException e) {
            throw new ClientException(e);
        }
    }

    private static void deleteAddress(Scanner scanner) {
        System.out.print("Enter address ID to delete: ");
        var id = scanner.nextLine();
        var request = rawHttp.parseRequest(
                "DELETE /addresses/" + id + " HTTP/1.1\r\n" +
                        "Host: " + HOST + "\r\n"
        );
        try (var socket = new Socket(HOST, PORT)) {
            request.writeTo(socket.getOutputStream());
            var response = rawHttp.parseResponse(socket.getInputStream()).eagerly();
            printResponseInfo(response);
        } catch (IOException e) {
            throw new ClientException(e);
        }
    }

    private static void printAddress(Address address) {
        System.out.println("ID: " + address.getId());
        System.out.println("Street: " + address.getStreet());
        System.out.println("City: " + address.getCity());
        System.out.println("State: " + address.getState());
        System.out.println("Zip Code: " + address.getZipCode());
        System.out.println("Country: " + address.getCountry());
    }

    private static void printResponseInfo(RawHttpResponse<?> response) {
        System.out.println("Status: " + response.getStatusCode());
        System.out.println("Message: " + response.getStartLine().getReason());
    }
}
