package cat.uvic.teknos.dam.miruvic.server.exceptions;

public class ConflictException extends HttpException {
    public ConflictException(String message) {
        super(409, message);
    }
}