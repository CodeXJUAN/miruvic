package cat.uvic.teknos.dam.miruvic.jdbc.exceptions;

public class CrudException extends RuntimeException {
    public CrudException() {
    }

    public CrudException(String message) {
        super(message);
    }

    public CrudException(String message, Throwable cause) {
        super(message, cause);
    }

    public CrudException(Throwable cause) {
        super(cause);
    }

    public CrudException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
