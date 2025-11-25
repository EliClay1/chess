package client;

import java.util.List;
import java.util.Map;

public class UserState {

    private static String host;
    private static int port;
    private static String authToken;
    private static String username;
    private static boolean loggedIn;
    private static List<Map<String, String>> activeGames;

    public UserState(String host, int port, String authToken, String username, boolean loggedIn, List<Map<String, String>> activeGames) {
        setHost(host);
        setPort(port);
        setAuthToken(authToken);
        setUsername(username);
        setLoggedIn(loggedIn);
        setActiveGames(activeGames);
}

    public List<Map<String, String>> getActiveGames() {
        return activeGames;
    }

    public void setActiveGames(List<Map<String, String>> activeGames) {
        UserState.activeGames = activeGames;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        UserState.loggedIn = loggedIn;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        UserState.username = username;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        UserState.authToken = authToken;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        UserState.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        UserState.host = host;
    }
}
