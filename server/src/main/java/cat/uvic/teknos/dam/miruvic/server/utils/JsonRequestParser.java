package cat.uvic.teknos.dam.miruvic.server.utils;

import cat.uvic.teknos.dam.miruvic.server.exceptions.BadRequestException;
import cat.uvic.teknos.dam.miruvic.server.exceptions.InternalServerErrorException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rawhttp.core.RawHttpRequest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public record JsonRequestParser(ObjectMapper objectMapper) {

    private static final Logger logger = LoggerFactory.getLogger(JsonRequestParser.class);

    public String extractBody(RawHttpRequest request) {
        if (!request.getBody().isPresent()) {
            throw new BadRequestException("Request body is required");
        }

        try {
            return request.getBody().get().decodeBodyToString(StandardCharsets.UTF_8);
        } catch (Exception e) {
            logger.error("Error decoding request body", e);
            throw new BadRequestException("Invalid request body: " + e.getMessage());
        }
    }

    public JsonNode parseJson(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (IOException e) {
            logger.error("Error parsing JSON", e);
            throw new BadRequestException("Invalid JSON format: " + e.getMessage());
        }
    }

    public JsonNode parseRequestBody(RawHttpRequest request) {
        String json = extractBody(request);
        return parseJson(json);
    }

    public String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (IOException e) {
            logger.error("Error serializing object to JSON", e);
            throw new InternalServerErrorException("Error serializing object to JSON", e);
        }
    }

    public <T> T parseToClass(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            logger.error("Error parsing JSON to {}", clazz.getSimpleName(), e);
            throw new BadRequestException("Error parsing JSON to " + clazz.getSimpleName() + ": " + e.getMessage());
        }
    }
}