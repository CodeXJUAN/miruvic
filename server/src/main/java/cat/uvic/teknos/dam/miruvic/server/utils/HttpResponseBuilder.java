package cat.uvic.teknos.dam.miruvic.server.utils;

import cat.uvic.teknos.dam.miruvic.utils.security.CryptoUtils;
import cat.uvic.teknos.dam.miruvic.utils.security.SecurityConstants;
import rawhttp.core.RawHttp;
import rawhttp.core.RawHttpResponse;

import java.nio.charset.StandardCharsets;

public record HttpResponseBuilder() {

    private static final RawHttp RAW_HTTP = new RawHttp();
    private static final CryptoUtils CRYPTO_UTILS = new CryptoUtils();

    public RawHttpResponse<?> ok(String jsonBody) {
        String hash = CRYPTO_UTILS.hash(jsonBody);
        return RAW_HTTP.parseResponse(
                "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: application/json\r\n" +
                        SecurityConstants.HASH_HEADER + ": " + hash + "\r\n" +
                        "Content-Length: " + jsonBody.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                        "\r\n" +
                        jsonBody
        );
    }

    public RawHttpResponse<?> created() {
        return RAW_HTTP.parseResponse(
                "HTTP/1.1 201 Created\r\n" +
                        SecurityConstants.HASH_HEADER + ": " + CRYPTO_UTILS.hash("") + "\r\n" +
                        "Content-Length: 0\r\n" +
                        "\r\n"
        );
    }

    public RawHttpResponse<?> created(String location) {
        return RAW_HTTP.parseResponse(
                "HTTP/1.1 201 Created\r\n" +
                        "Location: " + location + "\r\n" +
                        SecurityConstants.HASH_HEADER + ": " + CRYPTO_UTILS.hash("") + "\r\n" +
                        "Content-Length: 0\r\n" +
                        "\r\n"
        );
    }

    public RawHttpResponse<?> noContent() {
        return RAW_HTTP.parseResponse(
                "HTTP/1.1 204 No Content\r\n" +
                        SecurityConstants.HASH_HEADER + ": " + CRYPTO_UTILS.hash("") + "\r\n" +
                        "Content-Length: 0\r\n" +
                        "\r\n"
        );
    }

    public RawHttpResponse<?> error(int statusCode, String message) {
        String statusText = getStatusText(statusCode);
        String body = "{\"error\": \"" + escapeJson(message) + "\"}";
        String hash = CRYPTO_UTILS.hash(body);

        return RAW_HTTP.parseResponse(
                "HTTP/1.1 " + statusCode + " " + statusText + "\r\n" +
                        "Content-Type: application/json\r\n" +
                        SecurityConstants.HASH_HEADER + ": " + hash + "\r\n" +
                        "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                        "\r\n" +
                        body
        );
    }

    public RawHttpResponse<?> success(int statusCode, String message) {
        String statusText = getStatusText(statusCode);
        String body = "{\"message\": \"" + escapeJson(message) + "\"}";
        String hash = CRYPTO_UTILS.hash(body);

        return RAW_HTTP.parseResponse(
                "HTTP/1.1 " + statusCode + " " + statusText + "\r\n" +
                        "Content-Type: application/json\r\n" +
                        SecurityConstants.HASH_HEADER + ": " + hash + "\r\n" +
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