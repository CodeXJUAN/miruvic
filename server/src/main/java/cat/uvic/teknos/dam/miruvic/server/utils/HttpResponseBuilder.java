package cat.uvic.teknos.dam.miruvic.server.utils;

import rawhttp.core.RawHttp;
import rawhttp.core.RawHttpResponse;

import java.nio.charset.StandardCharsets;

/**
 * HttpResponseBuilder - Responsabilidad: Construir respuestas HTTP en formato correcto.
 */
public record HttpResponseBuilder() {

    private static final RawHttp RAW_HTTP = new RawHttp();

    public RawHttpResponse<?> ok(String jsonBody) {
        return RAW_HTTP.parseResponse(
                "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: application/json\r\n" +
                        "Content-Length: " + jsonBody.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                        "\r\n" +
                        jsonBody
        );
    }

    public RawHttpResponse<?> created() {
        return RAW_HTTP.parseResponse(
                "HTTP/1.1 201 Created\r\n" +
                        "Content-Length: 0\r\n" +
                        "\r\n"
        );
    }

    public RawHttpResponse<?> created(String location) {
        return RAW_HTTP.parseResponse(
                "HTTP/1.1 201 Created\r\n" +
                        "Location: " + location + "\r\n" +
                        "Content-Length: 0\r\n" +
                        "\r\n"
        );
    }

    public RawHttpResponse<?> noContent() {
        return RAW_HTTP.parseResponse(
                "HTTP/1.1 204 No Content\r\n" +
                        "Content-Length: 0\r\n" +
                        "\r\n"
        );
    }

    public RawHttpResponse<?> error(int statusCode, String message) {
        String statusText = getStatusText(statusCode);
        String body = "{\"error\": \"" + escapeJson(message) + "\"}";

        return RAW_HTTP.parseResponse(
                "HTTP/1.1 " + statusCode + " " + statusText + "\r\n" +
                        "Content-Type: application/json\r\n" +
                        "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                        "\r\n" +
                        body
        );
    }

    public RawHttpResponse<?> success(int statusCode, String message) {
        String statusText = getStatusText(statusCode);
        String body = "{\"message\": \"" + escapeJson(message) + "\"}";

        return RAW_HTTP.parseResponse(
                "HTTP/1.1 " + statusCode + " " + statusText + "\r\n" +
                        "Content-Type: application/json\r\n" +
                        "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                        "\r\n" +
                        body
        );
    }

    private String getStatusText(int statusCode) {
        return switch (statusCode) {
            case 200 -> "OK";
            case 201 -> "Created";
            case 204 -> "No Content";
            case 400 -> "Bad Request";
            case 404 -> "Not Found";
            case 405 -> "Method Not Allowed";
            case 409 -> "Conflict";
            case 500 -> "Internal Server Error";
            default -> "Unknown";
        };
    }

    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}