package model;

public record AuthData() {

    static String username;
    static String authToken;

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        AuthData.username = username;
    }

    public static String getAuthToken() {
        return authToken;
    }

    public static void setAuthToken(String authToken) {
        AuthData.authToken = authToken;
    }

    @Override
    public String toString() {
        return "AuthData{}";
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
