import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Locale;

public class HttpClient {
    private static final java.net.http.HttpClient httpClient = java.net.http.HttpClient.newHttpClient();

    public void get(String host, int port, String path) throws Exception {
        String url = String.format(Locale.getDefault(), "http://%s:%d%s", host, port, path);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .timeout(java.time.Duration.ofMillis(5000))
                .GET()
                .build();

        // What in the heckerdundooskis is going on here.
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            System.out.print(response.body());
        } else {
            System.out.println("Error: recieved status code: " + response.statusCode());
        }
    }
}
