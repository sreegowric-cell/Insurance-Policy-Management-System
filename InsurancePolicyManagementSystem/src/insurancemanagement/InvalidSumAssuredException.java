package insurancemanagement;

public class InvalidSumAssuredException extends Exception {

    private double rejectedValue;

    public InvalidSumAssuredException(String message) {
        super(message);
    }

    public InvalidSumAssuredException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidSumAssuredException(String message, double value) {
        super(message);
        this.rejectedValue = value;
    }

    public double getRejectedValue() {
        return rejectedValue;
    }
}


