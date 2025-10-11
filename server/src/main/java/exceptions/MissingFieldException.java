package exceptions;

/**
 * Indicates there was an error with the input values
 */

public class MissingFieldException extends Exception {
    public MissingFieldException(String message) {
        super(message);
    }
    public MissingFieldException(String message, Throwable ex) {
        super(message, ex);
    }
}
