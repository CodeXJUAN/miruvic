package cat.uvic.teknos.dam.miruvic.server.exceptions;

public class InternalServerErrorException extends RuntimeException {
  public InternalServerErrorException(String message) {
    super(message);
  }
}
