package client;

import java.util.Scanner;

import static ui.EscapeSequences.RESET_TEXT_COLOR;

public class ChessClient {
    // TODO - transfer main code here.

//    public static UserStates loginState;
    public static UserState userState;

    public static void main(String[] args) {
        while (true) {
            Scanner scanner = new Scanner(System.in);



            if (!userState.loggedIn()) {
                simplePrint(6, "[Logged Out] >>> ");
            }








        }
    }

    private static void simplePrint(int colorID, String message) {
        System.out.printf("\u001b[38;5;%dm%s%s\n", colorID, message, RESET_TEXT_COLOR);
    }
}
