package client.results;

// turned into record by intellij.
public record CommandResult(boolean ok, String message) {
    public static CommandResult ok(String msg) {
        return new CommandResult(true, msg);
    }

    public static CommandResult error(String msg) {
        return new CommandResult(false, msg);
    }
}
