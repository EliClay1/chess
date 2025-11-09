package client.results;

public class ValidationResult {
    public final boolean ok;
    public final String message;

    public ValidationResult(boolean ok, String message) {
        this.ok = ok;
        this.message = message;
    }
    public ValidationResult ok() {
        return new ValidationResult(true, "");
    }
    public ValidationResult error() {
        return new ValidationResult(false, this.message);
    }
}
