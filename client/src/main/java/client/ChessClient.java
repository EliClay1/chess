package client;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class ChessClient {
    // TODO - transfer main code here.

    public static void main(String[] args) {
        UserState userState = new UserState("localhost", 8080, null, null, false);
        simplePrint(12, String.format("%sWelcome to Chess! Feel free to sign in, or type 'h' for help.%s\n\n",
                WHITE_KING, WHITE_QUEEN));

        while (true) {
            // determines log-in state for command inputs.
            String loginState = userState.loggedIn() ? "Logged In" : "Logged Out";
            simplePrint(6, String.format("[%s] >>> ", loginState));

            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            var listOfInputData = Arrays.stream(line.split(" ")).toList();
            String command = listOfInputData.getFirst();

        }
    }


    private static void simplePrint(int colorID, String message) {
        System.out.printf("\u001b[38;5;%dm%s%s", colorID, message, RESET_TEXT_COLOR);
    }
}
