package client;

public record UserState(String host, int port, String authToken, String username, boolean loggedIn) {
}
