package cat.uvic.teknos.dam.miruvic.client.services;

import cat.uvic.teknos.dam.miruvic.client.models.StudentDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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

public class StudentService {

    private final String host;
    private final int port;
    private final ObjectMapper objectMapper;
    private final RawHttp rawHttp;

    public StudentService(String host, int port, ObjectMapper objectMapper) {
        this.host = host;
        this.port = port;
        this.objectMapper = objectMapper;
        this.rawHttp = new RawHttp();
    }

    public void listAll() {
        System.out.println("\n Listando todos los estudiantes...");

        try (Socket socket = new Socket(host, port)) {
            RawHttpRequest request = rawHttp.parseRequest(
                    "GET /students HTTP/1.1\r\n" +
                            "Host: " + host + "\r\n" +
                            "Accept: application/json\r\n" +
                            "\r\n"
            );

            request.writeTo(socket.getOutputStream());
            RawHttpResponse<?> response = rawHttp.parseResponse(socket.getInputStream()).eagerly();

            if (response.getStatusCode() == 200) {
                // FIX: Usar decodeBodyToString en lugar de toString()
                String json = response.getBody().get().decodeBodyToString(StandardCharsets.UTF_8);
                StudentDTO[] students = objectMapper.readValue(json, StudentDTO[].class);

                if (students.length == 0) {
                    System.out.println("No hay estudiantes registrados.");
                } else {
                    System.out.println("Se encontraron " + students.length + " estudiantes:\n");
                    printStudentTable(Arrays.asList(students));
                }
            } else {
                System.out.println("Error: " + response.getStatusCode());
            }

        } catch (IOException e) {
            System.out.println("Error de conexión: " + e.getMessage());
        }
    }

    public void getById(Scanner scanner) {
        System.out.print("\n Ingrese el ID del estudiante: ");
        String id = scanner.nextLine().trim();

        try (Socket socket = new Socket(host, port)) {
            RawHttpRequest request = rawHttp.parseRequest(
                    "GET /students/" + id + " HTTP/1.1\r\n" +
                            "Host: " + host + "\r\n" +
                            "Accept: application/json\r\n" +
                            "\r\n"
            );

            request.writeTo(socket.getOutputStream());
            RawHttpResponse<?> response = rawHttp.parseResponse(socket.getInputStream()).eagerly();

            if (response.getStatusCode() == 200) {
                // FIX: Usar decodeBodyToString en lugar de toString()
                String json = response.getBody().get().decodeBodyToString(StandardCharsets.UTF_8);
                StudentDTO student = objectMapper.readValue(json, StudentDTO.class);

                System.out.println("\n✓ Estudiante encontrado:");
                printStudentDetails(student);
            } else if (response.getStatusCode() == 404) {
                System.out.println("No se encontró un estudiante con ID " + id);
            } else {
                System.out.println("Error: " + response.getStatusCode());
            }

        } catch (IOException e) {
            System.out.println("Error de conexión: " + e.getMessage());
        }
    }

    public void create(Scanner scanner) {
        System.out.println("\n Crear nuevo estudiante");

        System.out.print("Nombre: ");
        String firstName = scanner.nextLine().trim();

        System.out.print("Apellido: ");
        String lastName = scanner.nextLine().trim();

        System.out.print("Email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Contraseña: ");
        String password = scanner.nextLine().trim();

        System.out.print("Teléfono (opcional, Enter para omitir): ");
        String phone = scanner.nextLine().trim();

        System.out.print("ID de dirección (opcional, Enter para omitir): ");
        String addressIdStr = scanner.nextLine().trim();

        try (Socket socket = new Socket(host, port)) {

            ObjectNode jsonNode = objectMapper.createObjectNode();
            jsonNode.put("firstName", firstName);
            jsonNode.put("lastName", lastName);
            jsonNode.put("email", email);
            jsonNode.put("passwordHash", password);

            if (!phone.isEmpty()) {
                jsonNode.put("phoneNumber", phone);
            }

            if (!addressIdStr.isEmpty()) {
                try {
                    int addressId = Integer.parseInt(addressIdStr);
                    jsonNode.put("addressId", addressId);
                } catch (NumberFormatException e) {
                    System.out.println(" ID de dirección inválido, se omitirá.");
                }
            }

            String json = objectMapper.writeValueAsString(jsonNode);

            RawHttpRequest request = rawHttp.parseRequest(
                    "POST /students HTTP/1.1\r\n" +
                            "Host: " + host + "\r\n" +
                            "Content-Type: application/json\r\n" +
                            "Content-Length: " + json.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                            "\r\n"
            ).withBody(new StringBody(json));

            request.writeTo(socket.getOutputStream());
            RawHttpResponse<?> response = rawHttp.parseResponse(socket.getInputStream()).eagerly();

            if (response.getStatusCode() == 201) {
                System.out.println("✓ Estudiante creado exitosamente");
            } else {
                System.out.println("Error al crear: " + response.getStatusCode());
                if (response.getBody().isPresent()) {
                    System.out.println("   " + response.getBody().get().decodeBodyToString(StandardCharsets.UTF_8));
                }
            }

        } catch (IOException e) {
            System.out.println("Error de conexión: " + e.getMessage());
        }
    }

    public void update(Scanner scanner) {
        System.out.print("\n✏ Ingrese el ID del estudiante a actualizar: ");
        String id = scanner.nextLine().trim();

        System.out.print("Nuevo nombre: ");
        String firstName = scanner.nextLine().trim();

        System.out.print("Nuevo apellido: ");
        String lastName = scanner.nextLine().trim();

        System.out.print("Nuevo email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Nueva contraseña: ");
        String password = scanner.nextLine().trim();

        System.out.print("Nuevo teléfono (opcional, Enter para omitir): ");
        String phone = scanner.nextLine().trim();

        System.out.print("Nuevo ID de dirección (opcional, Enter para omitir): ");
        String addressIdStr = scanner.nextLine().trim();

        try (Socket socket = new Socket(host, port)) {
            ObjectNode jsonNode = objectMapper.createObjectNode();
            jsonNode.put("firstName", firstName);
            jsonNode.put("lastName", lastName);
            jsonNode.put("email", email);
            jsonNode.put("passwordHash", password);

            if (!phone.isEmpty()) {
                jsonNode.put("phoneNumber", phone);
            }

            if (!addressIdStr.isEmpty()) {
                try {
                    int addressId = Integer.parseInt(addressIdStr);
                    jsonNode.put("addressId", addressId);
                } catch (NumberFormatException e) {
                    System.out.println("ID de dirección inválido, se omitirá.");
                }
            }

            String json = objectMapper.writeValueAsString(jsonNode);

            RawHttpRequest request = rawHttp.parseRequest(
                    "PUT /students/" + id + " HTTP/1.1\r\n" +
                            "Host: " + host + "\r\n" +
                            "Content-Type: application/json\r\n" +
                            "Content-Length: " + json.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                            "\r\n"
            ).withBody(new StringBody(json));

            request.writeTo(socket.getOutputStream());
            RawHttpResponse<?> response = rawHttp.parseResponse(socket.getInputStream()).eagerly();

            if (response.getStatusCode() == 204) {
                System.out.println("Estudiante actualizado exitosamente");
            } else if (response.getStatusCode() == 404) {
                System.out.println("No se encontró un estudiante con ID " + id);
            } else {
                System.out.println("Error al actualizar: " + response.getStatusCode());
            }

        } catch (IOException e) {
            System.out.println("Error de conexión: " + e.getMessage());
        }
    }

    public void delete(Scanner scanner) {
        System.out.print("\n Ingrese el ID del estudiante a eliminar: ");
        String id = scanner.nextLine().trim();

        System.out.print("¿Está seguro? (s/n): ");
        String confirm = scanner.nextLine().trim().toLowerCase();

        if (!confirm.equals("s")) {
            System.out.println("Operación cancelada.");
            return;
        }

        try (Socket socket = new Socket(host, port)) {
            RawHttpRequest request = rawHttp.parseRequest(
                    "DELETE /students/" + id + " HTTP/1.1\r\n" +
                            "Host: " + host + "\r\n" +
                            "\r\n"
            );

            request.writeTo(socket.getOutputStream());
            RawHttpResponse<?> response = rawHttp.parseResponse(socket.getInputStream()).eagerly();

            if (response.getStatusCode() == 204) {
                System.out.println("Estudiante eliminado exitosamente");
            } else if (response.getStatusCode() == 404) {
                System.out.println("No se encontró un estudiante con ID " + id);
            } else {
                System.out.println("Error al eliminar: " + response.getStatusCode());
            }

        } catch (IOException e) {
            System.out.println("Error de conexión: " + e.getMessage());
        }
    }

    private void printStudentTable(List<StudentDTO> students) {
        System.out.println("┌──────┬──────────────────┬──────────────────┬─────────────────────────────┬──────────────┬────────────┐");
        System.out.println("│  ID  │     Nombre       │    Apellido      │           Email             │   Teléfono   │ Address ID │");
        System.out.println("├──────┼──────────────────┼──────────────────┼─────────────────────────────┼──────────────┼────────────┤");

        for (StudentDTO student : students) {
            System.out.printf("│ %-4d │ %-16s │ %-16s │ %-27s │ %-12s │ %-10s │%n",
                    student.getId() != null ? student.getId() : 0,
                    truncate(student.getFirstName(), 16),
                    truncate(student.getLastName(), 16),
                    truncate(student.getEmail(), 27),
                    truncate(student.getPhoneNumber(), 12),
                    student.getAddress() != null && student.getAddress().getId() != null ?
                            student.getAddress().getId().toString() : "N/A"
            );
        }

        System.out.println("└──────┴──────────────────┴──────────────────┴─────────────────────────────┴──────────────┴────────────┘");
    }

    private void printStudentDetails(StudentDTO student) {
        System.out.println("┌────────────────────────────────────────────────┐");
        System.out.println("│        DETALLES DEL ESTUDIANTE                │");
        System.out.println("├────────────────────────────────────────────────┤");
        System.out.println("│ ID:         " + student.getId());
        System.out.println("│ Nombre:     " + student.getFirstName());
        System.out.println("│ Apellido:   " + student.getLastName());
        System.out.println("│ Email:      " + student.getEmail());
        System.out.println("│ Teléfono:   " + (student.getPhoneNumber() != null ? student.getPhoneNumber() : "N/A"));

        if (student.getAddress() != null) {
            System.out.println("│ Dirección:");
            System.out.println("│   - ID:     " + student.getAddress().getId());
            System.out.println("│   - Calle:  " + student.getAddress().getStreet());
            System.out.println("│   - Ciudad: " + student.getAddress().getCity());
            System.out.println("│   - País:   " + student.getAddress().getCountry());
        } else {
            System.out.println("│ Dirección:  Sin dirección asignada");
        }

        System.out.println("└────────────────────────────────────────────────┘");
    }

    private String truncate(String str, int length) {
        if (str == null) return "";
        return str.length() > length ? str.substring(0, length - 3) + "..." : str;
    }
}