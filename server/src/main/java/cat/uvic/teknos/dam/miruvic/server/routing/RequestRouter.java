package cat.uvic.teknos.dam.miruvic.server.routing;

import cat.uvic.teknos.dam.miruvic.server.controllers.AddressController;
import cat.uvic.teknos.dam.miruvic.server.controllers.StudentController;
import cat.uvic.teknos.dam.miruvic.server.exceptions.HttpException;
import rawhttp.core.RawHttp;
import rawhttp.core.RawHttpRequest;
import rawhttp.core.RawHttpResponse;

public class RequestRouter {

    private final RawHttp rawHttp;
    private final AddressController addressController;
    private final StudentController studentController;

    public RequestRouter(AddressController addressController, StudentController studentController) {
        this.rawHttp = new RawHttp();
        this.addressController = addressController;
        this.studentController = studentController;
    }

    public RawHttpResponse<?> route(RawHttpRequest request) {
        try {
            String path = request.getUri().getPath();
            String method = request.getMethod();

            System.out.println("Request: " + method + " " + path);

            if (path.equals("/addresses") || path.startsWith("/addresses/")) {
                return routeAddresses(request, path, method);
            }

            throw new HttpException(404, "Endpoint not found");

        } catch (HttpException e) {
            return createErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return createErrorResponse(500, "Internal server error: " + e.getMessage());
        }
    }

    private RawHttpResponse<?> routeAddresses(RawHttpRequest request, String path, String method) {
        if (path.equals("/addresses") && method.equals("GET")) {
            return addressController.getAll(request);
        }

        if (path.matches("/addresses/\\d+") && method.equals("GET")) {
            return addressController.get(request);
        }

        if (path.equals("/addresses") && method.equals("POST")) {
            return addressController.post(request);
        }

        if (path.matches("/addresses/\\d+") && method.equals("PUT")) {
            return addressController.put(request);
        }

        if (path.matches("/addresses/\\d+") && method.equals("DELETE")) {
            return addressController.delete(request);
        }

        throw new HttpException(405, "Method not allowed");
    }

    private RawHttpResponse<?> createErrorResponse(int statusCode, String message) {
        String statusText = getStatusText(statusCode);
        String body = "{\"error\": \"" + message + "\"}";

        return rawHttp.parseResponse(
                "HTTP/1.1 " + statusCode + " " + statusText + "\r\n" +
                        "Content-Type: application/json\r\n" +
                        "Content-Length: " + body.length() + "\r\n" +
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
            case 500 -> "Internal Server Error";
            default -> "Unknown";
        };
    }
}