import client.ChessClient;

public class Main {

    /* TODO - Error handling, specifically every single kind of bad input, not the right amount of arguments
    * Wrong type of arguments, and rejected arguments (ie already created with that account name)
    * */

    public static void main(String[] args) {
        new ChessClient().run();
    }
}