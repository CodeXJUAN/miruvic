package cat.uvic.teknos.dam.miruvic.ui.exceptions;

public class BannerException extends RuntimeException {
    public BannerException() {
    }

    public BannerException(String message) {
      super(message);
    }

    public BannerException(String message, Throwable cause) {
      super(message, cause);
    }

    public BannerException(Throwable cause) {
      super(cause);
    }

    public BannerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
      super(message, cause, enableSuppression, writableStackTrace);
    }
}
