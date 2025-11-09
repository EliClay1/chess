package client;

public class UserState {

    private static String host;
    private static int port;
    private static String authToken;
    private static String username;
    private static boolean loggedIn;

    public UserState(String host, int port, String authToken, String username, boolean loggedIn) {
        setHost(host);
        setPort(port);
        setAuthToken(authToken);
        setUsername(username);
        setLoggedIn(loggedIn);
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public static void setLoggedIn(boolean loggedIn) {
        UserState.loggedIn = loggedIn;
    }

    public String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        UserState.username = username;
    }

    public String getAuthToken() {
        return authToken;
    }

    public static void setAuthToken(String authToken) {
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
