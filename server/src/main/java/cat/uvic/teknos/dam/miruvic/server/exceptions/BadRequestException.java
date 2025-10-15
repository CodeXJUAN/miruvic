package cat.uvic.teknos.dam.miruvic.server.exceptions;

public class BadRequestException extends HttpException {
    public BadRequestException(String message) {
        super(400, message);
    }
}