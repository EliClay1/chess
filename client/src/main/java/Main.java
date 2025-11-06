import chess.*;
import static ui.EscapeSequences.*;

import java.util.Arrays;
import java.util.Scanner;

public class Main {
    private static HttpClient httpClient = new HttpClient();

    public Main() {
        httpClient = new HttpClient();
    }


    public static void main(String[] args) throws Exception {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("Welcome to Chess! Feel free to sign in, or type 'h' for help.");
        boolean isActive = true;
        while (isActive) {
            System.out.printf("%sChessMaster4000 >>> %s", SET_TEXT_COLOR_LIGHT_GREY + SET_TEXT_ITALIC, RESET_TEXT_COLOR + RESET_TEXT_ITALIC);
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            var listOfInputData = Arrays.stream(line.split(" ")).toList();
            String command = listOfInputData.getFirst();
            // TODO - Help command will change depending on if the user is logged in. Remember to place this in the helper function.
            if (command.equalsIgnoreCase("help") || command.equalsIgnoreCase("h")) {
                printHelpInformation(false);
            } else if (command.equalsIgnoreCase("register") || command.equalsIgnoreCase("r")) {
                System.out.print("Registering User:\n");
                httpClient.get("localhost", 8080, "/register");
                // run argument check helper function
            } else if (command.equalsIgnoreCase("login") || command.equalsIgnoreCase("l")) {
                System.out.print("Logging in User:\n");
                // run argument check helper function
            } else if (command.equalsIgnoreCase("quit") || command.equalsIgnoreCase("q")) {
                System.out.print("Exiting Chess...");
                isActive = false;
            } else {
                System.out.print("I'm sorry, but I don't know that command. Please try again, or type 'h' for a list of commands.\n");
            }
        }
    }

    private static void printHelpInformation(boolean loggedIn) {
        if (!loggedIn) {
            System.out.printf("%sOptions:\n", SET_TEXT_COLOR_BLUE);
            System.out.print("Login as an existing user: \"l\", \"login\" <USERNAME> <PASSWORD>\n");
            System.out.print("Register as a new user: \"r\", \"register\" <USERNAME> <PASSWORD> <EMAIL>\n");
            System.out.print("Exit the program: \"q\", \"quit\"\n");
            System.out.print("Print this message: \"p\", \"print\"\n");
            return;
        }
        System.out.print("Options:\n");
        System.out.print("List current games: \"l\", \"list\"\n");
        System.out.print("Create a new game: \"c\", \"create\" <GAME NAME>\n");
        System.out.print("Join an existing game: \"j\", \"join\" <GAME ID> <COLOR>\n");
        System.out.print("Watch a game: \"w\", \"watch\" <GAME ID>\n");
        System.out.print("Logout: \"logout\"\n");
        System.out.print("Print this message: \"p\", \"print\"\n");

    }
}