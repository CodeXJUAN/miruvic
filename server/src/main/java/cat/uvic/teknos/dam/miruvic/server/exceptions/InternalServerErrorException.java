package cat.uvic.teknos.dam.miruvic.server.exceptions;

public class InternalServerErrorException extends HttpException {
    public InternalServerErrorException(String message) {
        super(500, message);
    }

    public InternalServerErrorException(String message, Throwable cause) {
        super(500, message);
        initCause(cause);
    }
}