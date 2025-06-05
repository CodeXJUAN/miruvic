package cat.uvic.teknos.dam.miruvic.jdbc.exceptions;

public class DataSourceException extends RuntimeException {
    public DataSourceException(String message, Throwable cause) {
        super(message, cause);
    }
}