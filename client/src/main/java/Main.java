import client.ServerFacade;
import exceptions.InvalidException;

import static ui.EscapeSequences.*;

import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static ServerFacade httpClient = new ServerFacade();
    private static String authToken;
    private static String user;

    public Main() {
        httpClient = new ServerFacade();
    }

    /* TODO - Error handling, specifically every single kind of bad input, not the right amount of arguments
    * Wrong type of arguments, and rejected arguments (ie already created with that account name)
    * */

    public static void main(String[] args) throws Exception {
//        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("Welcome to Chess! Feel free to sign in, or type 'h' for help.");
        boolean isActive = true;
        boolean loggedIn = false;
        while (isActive) {
//            printHelpInformation(false);
            System.out.printf("%sChessMaster4000 >>> %s", SET_TEXT_COLOR_LIGHT_GREY + SET_TEXT_ITALIC, RESET_TEXT_COLOR + RESET_TEXT_ITALIC);
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            var listOfInputData = Arrays.stream(line.split(" ")).toList();
            String command = listOfInputData.getFirst();
            if (command.equalsIgnoreCase("help") || command.equalsIgnoreCase("h")) {
                printHelpInformation(false);
            } else if (command.equalsIgnoreCase("register") || command.equalsIgnoreCase("r")) {
                try {
                    if (listOfInputData.size() > 4) {
                        throw new IllegalArgumentException();
                    }
                    String username = listOfInputData.get(1);
                    String password = listOfInputData.get(2);
                    String email = listOfInputData.get(3);
                    httpClient.registerUser("localhost", 8080, "/user", username, password, email);
                    // if correct registration information provided.
                    loggedIn = true;
                } catch(Exception e) {
                    if (e instanceof ArrayIndexOutOfBoundsException) {
                        simplePrint(1, "Not enough arguments. Try again.");
                    } else if (e instanceof IllegalArgumentException) {
                        simplePrint(1, "Too many arguments. Try again");
                    } else if (e instanceof InvalidException) {
                        simplePrint(1, "Invalid characters. Try again.");
                    }
                }
            } else if (command.equalsIgnoreCase("login") || command.equalsIgnoreCase("l")) {

                try {
                    if (listOfInputData.size() > 4) {
                        throw new IllegalArgumentException();
                    }
                    String username = listOfInputData.get(1);
                    String password = listOfInputData.get(2);
                    Map<String, String> body = httpClient.loginUser("localhost", 8080, "/session", username, password);

                    authToken = body.get("authToken");
                    user = body.get("username");

                    // if correct registration information provided.
                    loggedIn = true;
                } catch(Exception e) {
                    if (e instanceof ArrayIndexOutOfBoundsException) {
                        simplePrint(1, "Not enough arguments. Try again.");
                    } else if (e instanceof IllegalArgumentException) {
                        simplePrint(1, "Too many arguments. Try again");
                    } else if (e instanceof InvalidException) {
                        simplePrint(1, "Invalid characters. Try again.");
                    }
                }

            } else if (command.equalsIgnoreCase("quit") || command.equalsIgnoreCase("q")) {
                simplePrint(12, "Exiting Chess...");
                isActive = false;
            } else {
                System.out.print("I'm sorry, but I don't know that command.\n");
            }

            // TODO - This is absolutely designed wrong, go back and fix it.
            while (loggedIn) {
                // TODO - Fix duplicate code here.
//                printHelpInformation(true);
                System.out.printf("%sChessMaster4000 >>> %s", SET_TEXT_COLOR_LIGHT_GREY + SET_TEXT_ITALIC, RESET_TEXT_COLOR + RESET_TEXT_ITALIC);
                Scanner loggedInScanner = new Scanner(System.in);
                String loggedInLine = loggedInScanner.nextLine();
                var loggedInListOfInputData = Arrays.stream(loggedInLine.split(" ")).toList();
                String loggedInCommand = loggedInListOfInputData.getFirst();
                if (loggedInCommand.equalsIgnoreCase("help") || loggedInCommand.equalsIgnoreCase("h")) {
                    printHelpInformation(true);
                } else if (loggedInCommand.equalsIgnoreCase("logout")) {
                    httpClient.logoutUser("localhost", 8080, "/session", authToken);
                    loggedIn = false;
                } else if (loggedInCommand.equalsIgnoreCase("list") || loggedInCommand.equalsIgnoreCase("l")) {
                    httpClient.listGames("localhost", 8080, "/game", authToken);
                } else if (loggedInCommand.equalsIgnoreCase("create") || loggedInCommand.equalsIgnoreCase("c")) {
                    // TODO - add error handling for excessive arguments, makes sure it's on all of them.
                    String gameName = loggedInListOfInputData.get(1);
                    httpClient.createGame("localhost", 8080, "/game", authToken, gameName);
                } else if (loggedInCommand.equalsIgnoreCase("join") || loggedInCommand.equalsIgnoreCase("j")) {
                    // TODO - add error handling for excessive arguments, makes sure it's on all of them.
                    try {
                        String gameID = loggedInListOfInputData.get(1);
                        String playerColor = loggedInListOfInputData.get(2);
                        httpClient.joinGame("localhost", 8080, "/game", authToken, gameID, playerColor);
                    } catch (Exception e) {
                        simplePrint(1, "Invalid Color. Try again");
                    }
                } else if (loggedInCommand.equalsIgnoreCase("watch") || loggedInCommand.equalsIgnoreCase("w")) {
                    try {
                        String gameID = loggedInListOfInputData.get(1);
                        httpClient.observeGame(gameID, null);
                    } catch (Exception e) {
                        simplePrint(1, "Error. Try again");
                    }
                }
            }
        }
    }

    private static void printHelpInformation(boolean loggedIn) {
        if (!loggedIn) {
            System.out.printf("%sOptions:\n", SET_TEXT_COLOR_BLUE);
            System.out.print("Login as an existing user: \"l\", \"login\" <USERNAME> <PASSWORD>\n");
            System.out.print("Register as a new user: \"r\", \"register\" <USERNAME> <PASSWORD> <EMAIL>\n");
            System.out.print("Exit the program: \"q\", \"quit\"\n");
            System.out.print("Print this message: \"h\", \"help\"\n");
            return;
        }
        System.out.printf("%sOptions:\n", SET_TEXT_COLOR_BLUE);
        System.out.print("List current games: \"l\", \"list\"\n");
        System.out.print("Create a new game: \"c\", \"create\" <GAME NAME>\n");
        System.out.print("Join an existing game: \"j\", \"join\" <GAME ID> <COLOR>\n");
        System.out.print("Watch a game: \"w\", \"watch\" <GAME ID>\n");
        System.out.print("Logout: \"logout\"\n");
        System.out.print("Print this message: \"h\", \"help\"\n");
    }

    private static void simplePrint(int colorID, String message) {
        System.out.printf("\u001b[38;5;%dm%s%s\n", colorID, message, RESET_TEXT_COLOR);
    }
}