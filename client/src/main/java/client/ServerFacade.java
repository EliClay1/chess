package client;

import exceptions.InvalidException;

import static ui.EscapeSequences.*;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Locale;

// TODO - implement port into initialization.
public class ServerFacade {
    private static final java.net.http.HttpClient httpClient = java.net.http.HttpClient.newHttpClient();

    public int status;

    public void registerUser(String host, int port, String path, String username,
                             String password, String email) throws Exception {

        // TODO - putting a # causes the password input and email inputs to break. Check to ensure those characters aren't in the email.
        if (invalidCharacters(password) || invalidCharacters(email) || invalidCharacters(username)) {
            throw new InvalidException();
        }
        // TODO - This makes it impossible to send a bad password, but the double handling checks never hurt anyone.

        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(
                String.format("{\"username\": \"%s\", \"password\": \"%s\", \"email\": \"%s\"}", username, password, email));
        String url = String.format(Locale.getDefault(), "http://%s:%d%s", host, port, path);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .timeout(Duration.ofMillis(5000))
                .POST(body)
                .build();

        // What in the heckerdundooskis is going on here.
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        status = response.statusCode();

        if (status >= 200 && status < 300) {
            System.out.printf("%sSuccessfully registed!\n%s",
                    SET_TEXT_COLOR_MAGENTA, RESET_TEXT_COLOR);
        } else if (status == 403) {
            System.out.printf("%sThat user already exists! Try again.\n%s",
                    "\u001b[38;5;1m", RESET_TEXT_COLOR);
        } else if (status == 406) {
            System.out.printf("%sBad inputs! Try again.\n%s",
                    "\u001b[38;5;1m", RESET_TEXT_COLOR);
        } else if (status == 500) {
            System.out.printf("%sAn error occurred. Please try again.\n%s",
                    "\u001b[38;5;1m", RESET_TEXT_COLOR);
        }
        else {
            System.out.println("Error: recieved status code: " + response.statusCode());
        }
    }

    // TODO - duplicate code.
    public void loginUser(String host, int port, String path, String username,
                             String password) throws Exception {

        if (invalidCharacters(password) || invalidCharacters(username)) {
            throw new InvalidException();
        }

        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(
                String.format("{\"username\": \"%s\", \"password\": \"%s\"}", username, password));
        String url = String.format(Locale.getDefault(), "http://%s:%d%s", host, port, path);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .timeout(Duration.ofMillis(5000))
                .POST(body)
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        status = response.statusCode();

        if (status >= 200 && status < 300) {
            System.out.printf("%sYou are now logged in!\n%s",
                    SET_TEXT_COLOR_MAGENTA, RESET_TEXT_COLOR);
        } else if (status == 401) {
            System.out.printf("%sIncorrect Password! Try again.\n%s",
                    "\u001b[38;5;1m", RESET_TEXT_COLOR);
        } else if (status == 406) {
            System.out.printf("%sBad inputs! Try again.\n%s",
                    "\u001b[38;5;1m", RESET_TEXT_COLOR);
        } else if (status == 500) {
            System.out.printf("%sAn error occurred. Please try again.\n%s",
                    "\u001b[38;5;1m", RESET_TEXT_COLOR);
        }
        else {
            System.out.println("Error: recieved status code: " + response.statusCode());
        }
    }

    public boolean invalidCharacters(String string) {
        String[] invalidCharacters = {"\"", " ", "#", "%", "&", "<", ">", "{", "}", "|", "\\", "~", "`", "'", "/", "="};
        for (var character : invalidCharacters) {
            if (string.contains(character)) {
                return true;
            }
        }
        return false;

    }

// TEMPLATE CODE
//    public void get(String host, int port, String path) throws Exception {
//        String url = String.format(Locale.getDefault(), "http://%s:%d%s", host, port, path);
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(new URI(url))
//                .timeout(java.time.Duration.ofMillis(5000))
//                .GET()
//                .build();
//
//        // What in the heckerdundooskis is going on here.
//        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
//
//        if (response.statusCode() >= 200 && response.statusCode() < 300) {
//            System.out.print(response.body());
//        } else {
//            System.out.println("Error: recieved status code: " + response.statusCode());
//        }
//    }
}
