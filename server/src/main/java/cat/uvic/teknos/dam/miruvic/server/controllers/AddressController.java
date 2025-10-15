package cat.uvic.teknos.dam.miruvic.server.controllers;

import cat.uvic.teknos.dam.miruvic.model.Address;
import cat.uvic.teknos.dam.miruvic.repositories.AddressRepository;
import cat.uvic.teknos.dam.miruvic.server.exceptions.NotFoundException;
import cat.uvic.teknos.dam.miruvic.server.factories.ModelFactory;
import cat.uvic.teknos.dam.miruvic.server.utils.HttpResponseBuilder;
import cat.uvic.teknos.dam.miruvic.server.utils.JsonRequestParser;
import cat.uvic.teknos.dam.miruvic.server.utils.PathParser;
import com.fasterxml.jackson.databind.JsonNode;
import rawhttp.core.RawHttpRequest;
import rawhttp.core.RawHttpResponse;

import java.util.Set;

public class AddressController {

    private final AddressRepository repository;
    private final ModelFactory modelFactory;
    private final JsonRequestParser jsonParser;
    private final HttpResponseBuilder responseBuilder;
    private final PathParser pathParser;

    public AddressController(
            AddressRepository repository,
            ModelFactory modelFactory,
            JsonRequestParser jsonParser,
            HttpResponseBuilder responseBuilder,
            PathParser pathParser) {
        this.repository = repository;
        this.modelFactory = modelFactory;
        this.jsonParser = jsonParser;
        this.responseBuilder = responseBuilder;
        this.pathParser = pathParser;
    }

    public RawHttpResponse<?> get(RawHttpRequest request) {
        int id = pathParser.extractIdFromPath(request.getUri().getPath());

        Address address = repository.get(id);

        if (address == null) {
            throw new NotFoundException("Address with ID " + id + " not found");
        }

        String json = jsonParser.toJson(address);
        return responseBuilder.ok(json);
    }

    public RawHttpResponse<?> getAll(RawHttpRequest request) {
        Set<Address> addresses = repository.getAll();
        String json = jsonParser.toJson(addresses);
        return responseBuilder.ok(json);
    }

    public RawHttpResponse<?> post(RawHttpRequest request) {
        JsonNode jsonNode = jsonParser.parseRequestBody(request);

        Address address = modelFactory.createAddress();
        populateAddressFromJson(address, jsonNode);

        repository.save(address);

        return responseBuilder.created();
    }

    public RawHttpResponse<?> put(RawHttpRequest request) {
        int id = pathParser.extractIdFromPath(request.getUri().getPath());

        Address existingAddress = repository.get(id);
        if (existingAddress == null) {
            throw new NotFoundException("Address with ID " + id + " not found");
        }

        JsonNode jsonNode = jsonParser.parseRequestBody(request);

        Address updatedAddress = modelFactory.createAddress();
        updatedAddress.setId(id);
        populateAddressFromJson(updatedAddress, jsonNode);

        repository.save(updatedAddress);

        return responseBuilder.noContent();
    }

    public RawHttpResponse<?> delete(RawHttpRequest request) {
        int id = pathParser.extractIdFromPath(request.getUri().getPath());

        Address address = repository.get(id);
        if (address == null) {
            throw new NotFoundException("Address with ID " + id + " not found");
        }

        repository.delete(address);

        return responseBuilder.noContent();
    }

    private void populateAddressFromJson(Address address, JsonNode jsonNode) {
        if (jsonNode.has("street")) {
            address.setStreet(jsonNode.get("street").asText());
        }
        if (jsonNode.has("city")) {
            address.setCity(jsonNode.get("city").asText());
        }
        if (jsonNode.has("state")) {
            address.setState(jsonNode.get("state").asText());
        }
        if (jsonNode.has("zipCode")) {
            address.setZipCode(jsonNode.get("zipCode").asText());
        }
        if (jsonNode.has("country")) {
            address.setCountry(jsonNode.get("country").asText());
        }
    }
}