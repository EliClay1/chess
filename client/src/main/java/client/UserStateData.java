package client;

import java.util.List;
import java.util.Map;

public class UserStateData {

    private static String host;
    private static int port;
    private static String authToken;
    private static String username;
    private static boolean loggedIn;
    private static List<Map<String, String>> activeGames;

    public UserStateData(String host, int port, String authToken, String username, List<Map<String, String>> activeGames) {
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
        UserStateData.activeGames = activeGames;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        UserStateData.loggedIn = loggedIn;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        UserStateData.username = username;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        UserStateData.authToken = authToken;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        UserStateData.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        UserStateData.host = host;
    }
}
