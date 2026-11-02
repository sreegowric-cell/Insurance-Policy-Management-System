package insurancemanagement;

public class InvalidTermException extends RuntimeException {

    public InvalidTermException(String message) {
        super(message);
    }

    public InvalidTermException(String message, Throwable cause) {
        super(message, cause);
    }
}
