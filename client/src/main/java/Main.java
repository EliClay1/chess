import chess.*;

import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("Welcome to Chess! Feel free to sign in, or type 'h' for help.");
        boolean isActive = true;
        while (isActive) {
            System.out.printf("ChessMaster400 >>> ");
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            var listOfInputData = Arrays.stream(line.split(" ")).toList();
            String command = listOfInputData.getFirst();
            // TODO - Help command will change depending on if the user is logged in. Remember to place this in the helper function.
            if (command.equalsIgnoreCase("help") || command.equalsIgnoreCase("h")) {
                System.out.print("Printing help List:\n");
            } else if (command.equalsIgnoreCase("register") || command.equalsIgnoreCase("r")) {
                System.out.print("Registering User:\n");
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
}