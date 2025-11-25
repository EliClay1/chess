import client.ChessClient;

public class Main {

    public static void main(String[] args) throws Exception {
        // TODO - put this within a try block, and raise an error when the databse fails.
        new ChessClient().run();
    }
}