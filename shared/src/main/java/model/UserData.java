package model;

public record UserData() {

    static String username;
    static String password;
    static String email;

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        UserData.username = username;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        UserData.password = password;
    }

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        UserData.email = email;
    }

    @Override
    public String toString() {
        return "UserData{}";
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
