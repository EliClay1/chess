package exceptions;

/**
 * Indicates there was an error with the input values
 */

public class AlreadyTakenException extends Exception {
    public AlreadyTakenException(String message) {
        super(message);
    }
    public AlreadyTakenException(String message, Throwable ex) {
        super(message, ex);
    }
}
