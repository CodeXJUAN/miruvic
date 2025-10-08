package cat.uvic.teknos.dam.miruvic.server.routing;

import cat.uvic.teknos.dam.miruvic.server.controllers.AddressController;
import cat.uvic.teknos.dam.miruvic.server.exceptions.HttpException;
import com.fasterxml.jackson.databind.ObjectMapper;
import rawhttp.core.RawHttp;
import rawhttp.core.RawHttpRequest;
import rawhttp.core.RawHttpResponse;

import java.io.IOException;

public class RequestRouter {

    private final RawHttp rawHttp;
    private final AddressController addressController;
    private final ObjectMapper objectMapper;

    public RequestRouter(AddressController addressController, ObjectMapper objectMapper) {
        this.rawHttp = new RawHttp();
        this.addressController = addressController;
        this.objectMapper = objectMapper;
    }

    public RawHttpResponse<?> route(RawHttpRequest request) throws IOException {
        var path = request.getUri().getPath();
        var method = request.getMethod();

        try {
            if (path.startsWith("/addresses")) {
                switch (method) {
                    case "GET":
                        var address = addressController.get(request);
                        var addressJson = objectMapper.writeValueAsString(address);
                        return rawHttp.parseResponse("HTTP/1.1 200 OK\r\n" +
                                "Content-Type: application/json\r\n" +
                                "Content-Length: " + addressJson.length() + "\r\n" +
                                "\r\n" +
                                addressJson);
                    case "POST":
                        addressController.post(request);
                        return rawHttp.parseResponse("HTTP/1.1 201 Created\r\n" +
                                "Content-Length: 0\r\n" +
                                "\r\n");
                    case "PUT":
                        addressController.put(request);
                        return rawHttp.parseResponse("HTTP/1.1 204 No Content\r\n" +
                                "Content-Length: 0\r\n" +
                                "\r\n");
                    case "DELETE":
                        addressController.delete(request);
                        return rawHttp.parseResponse("HTTP/1.1 204 No Content\r\n" +
                                "Content-Length: 0\r\n" +
                                "\r\n");
                    default:
                        return rawHttp.parseResponse("HTTP/1.1 405 Method Not Allowed\r\n" +
                                "Content-Length: 0\r\n" +
                                "\r\n");
                }
            }
        } catch (HttpException e) {
            return rawHttp.parseResponse("HTTP/1.1 " + e.getStatusCode() + " " + e.getMessage() + "\r\n" +
                    "Content-Length: 0\r\n" +
                    "\r\n");
        }

        return rawHttp.parseResponse("HTTP/1.1 404 Not Found\r\n" +
                "Content-Length: 0\r\n" +
                "\r\n");
    }
}