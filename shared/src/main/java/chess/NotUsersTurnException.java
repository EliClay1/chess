package chess;

public class NotUsersTurnException extends Exception {

    public NotUsersTurnException() {}

    public NotUsersTurnException(String message) {
        super(message);
    }
}
