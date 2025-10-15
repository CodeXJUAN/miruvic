package cat.uvic.teknos.dam.miruvic.server.exceptions;

public class MethodNotAllowedException extends HttpException {
    public MethodNotAllowedException(String message) {
        super(405, message);
    }
}