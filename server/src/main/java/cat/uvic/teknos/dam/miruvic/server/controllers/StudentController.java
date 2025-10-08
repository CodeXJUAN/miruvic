package cat.uvic.teknos.dam.miruvic.server.controllers;

import cat.uvic.teknos.dam.miruvic.model.Student;
import cat.uvic.teknos.dam.miruvic.model.Address;
import cat.uvic.teknos.dam.miruvic.model.impl.StudentImpl;
import cat.uvic.teknos.dam.miruvic.repositories.StudentRepository;
import cat.uvic.teknos.dam.miruvic.repositories.AddressRepository;
import cat.uvic.teknos.dam.miruvic.server.exceptions.HttpException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import rawhttp.core.RawHttp;
import rawhttp.core.RawHttpRequest;
import rawhttp.core.RawHttpResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class StudentController {

    private final StudentRepository studentRepository;
    private final AddressRepository addressRepository;
    private final ObjectMapper objectMapper;
    private final RawHttp rawHttp;

    public StudentController(StudentRepository studentRepository,
                             AddressRepository addressRepository,
                             ObjectMapper objectMapper) {
        this.studentRepository = studentRepository;
        this.addressRepository = addressRepository;
        this.objectMapper = objectMapper;
        this.rawHttp = new RawHttp();
    }

    public RawHttpResponse<?> get(RawHttpRequest request) {
        try {
            int id = extractIdFromPath(request.getUri().getPath());

            Student student = studentRepository.get(id);

            if (student == null) {
                throw new HttpException(404, "Student not found");
            }

            String json = objectMapper.writeValueAsString(student);

            return rawHttp.parseResponse(
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: application/json\r\n" +
                            "Content-Length: " + json.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                            "\r\n" +
                            json
            );

        } catch (IOException e) {
            throw new HttpException(500, "Error processing request: " + e.getMessage());
        }
    }

    public RawHttpResponse<?> getAll(RawHttpRequest request) {
        try {
            var students = studentRepository.getAll();

            String json = objectMapper.writeValueAsString(students);

            return rawHttp.parseResponse(
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: application/json\r\n" +
                            "Content-Length: " + json.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                            "\r\n" +
                            json
            );

        } catch (IOException e) {
            throw new HttpException(500, "Error processing request: " + e.getMessage());
        }
    }

    public RawHttpResponse<?> post(RawHttpRequest request) {
        try {
            if (!request.getBody().isPresent()) {
                throw new HttpException(400, "Request body is required");
            }

            String json = request.getBody().get().decodeBodyToString(StandardCharsets.UTF_8);

            JsonNode jsonNode = objectMapper.readTree(json);

            Student student = new StudentImpl();
            student.setFirstName(jsonNode.get("firstName").asText());
            student.setLastName(jsonNode.get("lastName").asText());
            student.setEmail(jsonNode.get("email").asText());
            student.setPasswordHash(jsonNode.get("passwordHash").asText());

            if (jsonNode.has("phoneNumber") && !jsonNode.get("phoneNumber").isNull()) {
                student.setPhoneNumber(jsonNode.get("phoneNumber").asText());
            }

            if (jsonNode.has("addressId") && !jsonNode.get("addressId").isNull()) {
                int addressId = jsonNode.get("addressId").asInt();
                Address address = addressRepository.get(addressId);

                if (address == null) {
                    throw new HttpException(400, "Address with ID " + addressId + " not found");
                }

                student.setAddress(address);
            }

            studentRepository.save(student);

            return rawHttp.parseResponse(
                    "HTTP/1.1 201 Created\r\n" +
                            "Content-Length: 0\r\n" +
                            "\r\n"
            );

        } catch (IOException e) {
            throw new HttpException(500, "Error processing request: " + e.getMessage());
        }
    }

    public RawHttpResponse<?> put(RawHttpRequest request) {
        try {
            int id = extractIdFromPath(request.getUri().getPath());

            Student existingStudent = studentRepository.get(id);
            if (existingStudent == null) {
                throw new HttpException(404, "Student not found");
            }

            if (!request.getBody().isPresent()) {
                throw new HttpException(400, "Request body is required");
            }

            String json = request.getBody().get().decodeBodyToString(StandardCharsets.UTF_8);

            JsonNode jsonNode = objectMapper.readTree(json);

            Student updatedStudent = new StudentImpl();
            updatedStudent.setId(id); // Mantener el ID original
            updatedStudent.setFirstName(jsonNode.get("firstName").asText());
            updatedStudent.setLastName(jsonNode.get("lastName").asText());
            updatedStudent.setEmail(jsonNode.get("email").asText());
            updatedStudent.setPasswordHash(jsonNode.get("passwordHash").asText());

            if (jsonNode.has("phoneNumber") && !jsonNode.get("phoneNumber").isNull()) {
                updatedStudent.setPhoneNumber(jsonNode.get("phoneNumber").asText());
            }

            if (jsonNode.has("addressId") && !jsonNode.get("addressId").isNull()) {
                int addressId = jsonNode.get("addressId").asInt();
                Address address = addressRepository.get(addressId);

                if (address == null) {
                    throw new HttpException(400, "Address with ID " + addressId + " not found");
                }

                updatedStudent.setAddress(address);
            }

            studentRepository.save(updatedStudent);

            return rawHttp.parseResponse(
                    "HTTP/1.1 204 No Content\r\n" +
                            "Content-Length: 0\r\n" +
                            "\r\n"
            );

        } catch (IOException e) {
            throw new HttpException(500, "Error processing request: " + e.getMessage());
        }
    }

    public RawHttpResponse<?> delete(RawHttpRequest request) {
        int id = extractIdFromPath(request.getUri().getPath());

        Student student = studentRepository.get(id);
        if (student == null) {
            throw new HttpException(404, "Student not found");
        }

        studentRepository.delete(student);

        return rawHttp.parseResponse(
                "HTTP/1.1 204 No Content\r\n" +
                        "Content-Length: 0\r\n" +
                        "\r\n"
        );
    }

    private int extractIdFromPath(String path) {
        try {
            String[] parts = path.split("/");
            return Integer.parseInt(parts[parts.length - 1]);
        } catch (NumberFormatException e) {
            throw new HttpException(400, "Invalid ID format");
        }
    }
}