package cat.uvic.teknos.dam.miruvic.server.routing;

import cat.uvic.teknos.dam.miruvic.server.controllers.AddressController;
import cat.uvic.teknos.dam.miruvic.server.controllers.StudentController;
import cat.uvic.teknos.dam.miruvic.server.exceptions.HttpException;
import cat.uvic.teknos.dam.miruvic.server.exceptions.MethodNotAllowedException;
import cat.uvic.teknos.dam.miruvic.server.exceptions.NotFoundException;
import cat.uvic.teknos.dam.miruvic.server.utils.HttpResponseBuilder;
import cat.uvic.teknos.dam.miruvic.server.utils.PathParser;
import rawhttp.core.RawHttpRequest;
import rawhttp.core.RawHttpResponse;

public class RequestRouter {

    private final AddressController addressController;
    private final StudentController studentController;
    private final HttpResponseBuilder responseBuilder;
    private final PathParser pathParser;

    public RequestRouter(
            AddressController addressController,
            StudentController studentController,
            HttpResponseBuilder responseBuilder,
            PathParser pathParser) {
        this.addressController = addressController;
        this.studentController = studentController;
        this.responseBuilder = responseBuilder;
        this.pathParser = pathParser;
    }

    public RawHttpResponse<?> route(RawHttpRequest request) {
        try {
            String path = request.getUri().getPath();
            String method = request.getMethod();

            System.out.println("→ Request: " + method + " " + path);

            // Routing por recurso
            if (path.startsWith("/addresses")) {
                return routeAddresses(request, path, method);
            }

            if (path.startsWith("/students")) {
                return routeStudents(request, path, method);
            }

            throw new NotFoundException("Endpoint not found: " + path);

        } catch (HttpException e) {
            return responseBuilder.error(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return responseBuilder.error(500, "Internal server error: " + e.getMessage());
        }
    }

    private RawHttpResponse<?> routeAddresses(RawHttpRequest request, String path, String method) {
        boolean isCollection = pathParser.isCollectionPath(path, "addresses");
        boolean isResource = pathParser.isResourcePath(path, "addresses");

        if (isCollection && method.equals("GET")) {
            return addressController.getAll(request);
        }

        if (isResource && method.equals("GET")) {
            return addressController.get(request);
        }

        if (isCollection && method.equals("POST")) {
            return addressController.post(request);
        }

        if (isResource && method.equals("PUT")) {
            return addressController.put(request);
        }

        if (isResource && method.equals("DELETE")) {
            return addressController.delete(request);
        }

        throw new MethodNotAllowedException("Method " + method + " not allowed for " + path);
    }

    private RawHttpResponse<?> routeStudents(RawHttpRequest request, String path, String method) {
        boolean isCollection = pathParser.isCollectionPath(path, "students");
        boolean isResource = pathParser.isResourcePath(path, "students");

        if (isCollection && method.equals("GET")) {
            return studentController.getAll(request);
        }

        if (isResource && method.equals("GET")) {
            return studentController.get(request);
        }

        if (isCollection && method.equals("POST")) {
            return studentController.post(request);
        }

        if (isResource && method.equals("PUT")) {
            return studentController.put(request);
        }

        if (isResource && method.equals("DELETE")) {
            return studentController.delete(request);
        }

        throw new MethodNotAllowedException("Method " + method + " not allowed for " + path);

    }
}