package cat.uvic.teknos.dam.miruvic.server.controllers;

import cat.uvic.teknos.dam.miruvic.model.Address;
import cat.uvic.teknos.dam.miruvic.server.exceptions.HttpException;
import cat.uvic.teknos.dam.miruvic.repositories.AddressRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import rawhttp.core.RawHttpRequest;
import rawhttp.core.body.BodyReader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class AddressController {

    private final AddressRepository repository;
    private final ObjectMapper objectMapper;

    public AddressController(AddressRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    public Address get(RawHttpRequest request) {
        var id = Integer.parseInt(request.getUri().getPath().substring(request.getUri().getPath().lastIndexOf('/') + 1));
        var address = repository.get(id);
        if (address == null) {
            throw new HttpException(404, "Not Found");
        }
        return address;
    }

    public void post(RawHttpRequest request) throws IOException {
        var address = objectMapper.readValue(request.getBody().get().decodeBodyToString(StandardCharsets.UTF_8), Address.class);
        repository.save(address);
    }

    public void put(RawHttpRequest request) throws IOException {
        var id = Integer.parseInt(request.getUri().getPath().substring(request.getUri().getPath().lastIndexOf('/') + 1));
        var address = repository.get(id);
        if (address == null) {
            throw new HttpException(404, "Not Found");
        }
        var updatedAddress = objectMapper.readValue(request.getBody().get().decodeBodyToString(StandardCharsets.UTF_8), Address.class);
        updatedAddress.setId(id);
        repository.save(updatedAddress);
    }

    public void delete(RawHttpRequest request) {
        var id = Integer.parseInt(request.getUri().getPath().substring(request.getUri().getPath().lastIndexOf('/') + 1));
        var address = repository.get(id);
        if (address == null) {
            throw new HttpException(404, "Not Found");
        }
        repository.delete(address);
    }
}