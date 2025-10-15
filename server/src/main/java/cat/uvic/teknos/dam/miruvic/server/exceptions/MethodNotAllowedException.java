package cat.uvic.teknos.dam.miruvic.server.exceptions;

public class MethodNotAllowed extends HttpException {
    public MethodNotAllowed(String message) {
        super(405, message);
    }
}