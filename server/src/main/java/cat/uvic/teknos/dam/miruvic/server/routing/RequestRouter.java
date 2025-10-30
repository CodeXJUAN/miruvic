package cat.uvic.teknos.dam.miruvic.server.routing;

import cat.uvic.teknos.dam.miruvic.server.controllers.AddressController;
import cat.uvic.teknos.dam.miruvic.server.controllers.StudentController;
import cat.uvic.teknos.dam.miruvic.server.exceptions.HttpException;
import cat.uvic.teknos.dam.miruvic.server.exceptions.MethodNotAllowedException;
import cat.uvic.teknos.dam.miruvic.server.exceptions.NotFoundException;
import cat.uvic.teknos.dam.miruvic.server.utils.HttpResponseBuilder;
import cat.uvic.teknos.dam.miruvic.server.utils.PathParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rawhttp.core.RawHttpRequest;
import rawhttp.core.RawHttpResponse;

public record RequestRouter(
        AddressController addressController,
        StudentController studentController,
        HttpResponseBuilder responseBuilder,
        PathParser pathParser) {

    private static final Logger logger = LoggerFactory.getLogger(RequestRouter.class);

    public RawHttpResponse<?> route(RawHttpRequest request) {
        try {
            String path = request.getUri().getPath();
            String method = request.getMethod();

            logger.info("→ Routing: {} {}", method, path);

            // Routing por recurso
            if (path.startsWith("/addresses")) {
                return routeAddresses(request, path, method);
            }

            if (path.startsWith("/students")) {
                return routeStudents(request, path, method);
            }

            // Ruta de desconexión especial
            if (path.equals("/disconnect")) {
                logger.info("Cliente solicitó desconexión");
                return responseBuilder.success(200, "OK");
            }

            throw new NotFoundException("Endpoint not found: " + path);

        } catch (HttpException e) {
            logger.warn("HTTP Exception: {} - {}", e.getStatusCode(), e.getMessage());
            return responseBuilder.error(e.getStatusCode(), e.getMessage());

        } catch (Exception e) {
            logger.error("Unexpected error routing request", e);
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