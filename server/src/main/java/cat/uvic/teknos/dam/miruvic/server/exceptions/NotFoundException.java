package cat.uvic.teknos.dam.miruvic.server.exceptions;

public class NotFoundException extends RuntimeException {
  public NotFoundException(String message) {
    super(message);
  }
}
