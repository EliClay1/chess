import static ui.EscapeSequences.*;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Locale;

public class HttpClient {
    private static final java.net.http.HttpClient httpClient = java.net.http.HttpClient.newHttpClient();

    public void registerUser(String host, int port, String path, String username,
                             String password, String email) throws Exception {

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
        int status = response.statusCode();

        if (status >= 200 && status < 300) {
            System.out.printf("%sSuccessfully registed!\n%s",
                    SET_TEXT_COLOR_MAGENTA, RESET_TEXT_COLOR);
        } else if (status == 403) {
            System.out.printf("%sThat user already exists! Try again.\n%s",
                    "\u001b[38;5;1m", RESET_TEXT_COLOR);
        }
        else {
            System.out.println("Error: recieved status code: " + response.statusCode());
        }
    }

    // TODO - duplicate code.
    public void loginUser(String host, int port, String path, String username,
                             String password) throws Exception {

        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(
                String.format("{\"username\": \"%s\", \"password\": \"%s\"}", username, password));
        String url = String.format(Locale.getDefault(), "http://%s:%d%s", host, port, path);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .timeout(Duration.ofMillis(5000))
                .POST(body)
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            System.out.printf("%sSuccessfully logged in!\n%s",
                    SET_TEXT_COLOR_MAGENTA, RESET_TEXT_COLOR);
//            System.out.print(response.body());
        } else {
            System.out.println("Error: recieved status code: " + response.statusCode());
        }
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
