package client;

public record CommandResult(String message, int status) {
    String error(String msg) {
        return msg;
    };
    int statusCode(int code) {
        return code;
    }
}
