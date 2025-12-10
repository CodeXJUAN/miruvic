package cat.uvic.teknos.dam.miruvic.server.controllers;

import cat.uvic.teknos.dam.miruvic.model.Address;
import cat.uvic.teknos.dam.miruvic.model.Student;
import cat.uvic.teknos.dam.miruvic.repositories.AddressRepository;
import cat.uvic.teknos.dam.miruvic.repositories.StudentRepository;
import cat.uvic.teknos.dam.miruvic.server.exceptions.BadRequestException;
import cat.uvic.teknos.dam.miruvic.server.exceptions.NotFoundException;
import cat.uvic.teknos.dam.miruvic.server.factories.ModelFactory;
import cat.uvic.teknos.dam.miruvic.server.utils.HttpResponseBuilder;
import cat.uvic.teknos.dam.miruvic.server.utils.JsonRequestParser;
import cat.uvic.teknos.dam.miruvic.server.utils.PathParser;
import com.fasterxml.jackson.databind.JsonNode;
import rawhttp.core.RawHttpRequest;
import rawhttp.core.RawHttpResponse;

import java.util.Set;

public class StudentController {

    private final StudentRepository studentRepository;
    private final AddressRepository addressRepository;
    private final ModelFactory modelFactory;
    private final JsonRequestParser jsonParser;
    private final HttpResponseBuilder responseBuilder;
    private final PathParser pathParser;

    public StudentController(
            StudentRepository studentRepository,
            AddressRepository addressRepository,
            ModelFactory modelFactory,
            JsonRequestParser jsonParser,
            HttpResponseBuilder responseBuilder,
            PathParser pathParser) {
        this.studentRepository = studentRepository;
        this.addressRepository = addressRepository;
        this.modelFactory = modelFactory;
        this.jsonParser = jsonParser;
        this.responseBuilder = responseBuilder;
        this.pathParser = pathParser;
    }

    public RawHttpResponse<?> get(RawHttpRequest request) {
        int id = pathParser.extractIdFromPath(request.getUri().getPath());

        Student student = studentRepository.get(id);

        if (student == null) {
            throw new NotFoundException("Student with ID " + id + " not found");
        }

        String json = jsonParser.toJson(student);
        return responseBuilder.ok(json);
    }

    public RawHttpResponse<?> getAll(RawHttpRequest request) {
        Set<Student> students = studentRepository.getAll();
        String json = jsonParser.toJson(students);
        return responseBuilder.ok(json);
    }

    public RawHttpResponse<?> post(RawHttpRequest request) {
        JsonNode jsonNode = jsonParser.parseRequestBody(request);

        Student student = modelFactory.createStudent();
        populateStudentFromJson(student, jsonNode);

        studentRepository.save(student);

        return responseBuilder.created();
    }

    public RawHttpResponse<?> put(RawHttpRequest request) {
        int id = pathParser.extractIdFromPath(request.getUri().getPath());

        Student existingStudent = studentRepository.get(id);
        if (existingStudent == null) {
            throw new NotFoundException("Student with ID " + id + " not found");
        }

        JsonNode jsonNode = jsonParser.parseRequestBody(request);
        populateStudentFromJson(existingStudent, jsonNode);

        studentRepository.save(existingStudent);

        return responseBuilder.noContent();
    }

    public RawHttpResponse<?> delete(RawHttpRequest request) {
        int id = pathParser.extractIdFromPath(request.getUri().getPath());

        Student student = studentRepository.get(id);
        if (student == null) {
            throw new NotFoundException("Student with ID " + id + " not found");
        }

        studentRepository.delete(student);

        return responseBuilder.noContent();
    }

    private void populateStudentFromJson(Student student, JsonNode jsonNode) {
        if (jsonNode.has("firstName")) {
            student.setFirstName(jsonNode.get("firstName").asText());
        }

        if (jsonNode.has("lastName")) {
            student.setLastName(jsonNode.get("lastName").asText());
        }

        if (jsonNode.has("email")) {
            student.setEmail(jsonNode.get("email").asText());
        }

        if (jsonNode.has("passwordHash")) {
            student.setPasswordHash(jsonNode.get("passwordHash").asText());
        }

        if (jsonNode.has("phoneNumber") && !jsonNode.get("phoneNumber").isNull()) {
            student.setPhoneNumber(jsonNode.get("phoneNumber").asText());
        }

        if (jsonNode.has("addressId")) {
            if (jsonNode.get("addressId").isNull()) {
                student.setAddress(null);
            } else {
                int addressId = jsonNode.get("addressId").asInt();
                Address address = addressRepository.get(addressId);

                if (address == null) {
                    throw new BadRequestException("Address with ID " + addressId + " not found");
                }
                student.setAddress(address);
            }
        }
    }
}