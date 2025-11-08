public class ChessClient {

    public boolean loggedIn = false;
    private static ChessHttpClient httpClient;

    public ChessClient() {
        httpClient = new ChessHttpClient();
    }
}
