package cat.uvic.teknos.dam.miruvic.server.exceptions;

public class BadRequestException extends RuntimeException {
  public BadRequestException(String message) {
    super(message);
  }
}
