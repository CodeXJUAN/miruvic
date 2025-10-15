package cat.uvic.teknos.dam.miruvic.server.controllers;

import cat.uvic.teknos.dam.miruvic.model.Address;
import cat.uvic.teknos.dam.miruvic.model.impl.AddressImpl;
import cat.uvic.teknos.dam.miruvic.repositories.AddressRepository;
import cat.uvic.teknos.dam.miruvic.server.exceptions.HttpException;
import com.fasterxml.jackson.databind.ObjectMapper;
import rawhttp.core.RawHttp;
import rawhttp.core.RawHttpRequest;
import rawhttp.core.RawHttpResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class AddressController {

    private final AddressRepository repository;
    private final ObjectMapper objectMapper;
    private final RawHttp rawHttp;

    public AddressController(AddressRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
        this.rawHttp = new RawHttp();
    }

    public RawHttpResponse<?> get(RawHttpRequest request) {
        try {
            int id = extractIdFromPath(request.getUri().getPath());

            Address address = repository.get(id);

            if (address == null) {
                throw new HttpException(404, "Address not found");
            }

            String json = objectMapper.writeValueAsString(address);

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
            Set<Address> addresses = repository.getAll();

            String json = objectMapper.writeValueAsString(addresses);

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

            Address address = objectMapper.readValue(json, AddressImpl.class);

            repository.save(address);

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

            Address existingAddress = repository.get(id);
            if (existingAddress == null) {
                throw new HttpException(404, "Address not found");
            }

            if (!request.getBody().isPresent()) {
                throw new HttpException(400, "Request body is required");
            }

            String json = request.getBody().get().decodeBodyToString(StandardCharsets.UTF_8);

            Address updatedAddress = objectMapper.readValue(json, AddressImpl.class);

            updatedAddress.setId(id);

            repository.save(updatedAddress);

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

        Address address = repository.get(id);
        if (address == null) {
            throw new HttpException(404, "Address not found");
        }

        repository.delete(address);

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