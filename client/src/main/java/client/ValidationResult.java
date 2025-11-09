package client;

public record ValidationResult(String message, int status) {
    String error(String msg) {
        return msg;
    };
    int statusCode(int code) {
        return code;
    }
}
