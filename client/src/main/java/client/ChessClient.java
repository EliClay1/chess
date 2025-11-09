package client;

import client.commands.*;
import client.results.CommandResult;
import client.results.ValidationResult;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class ChessClient {

    public static void main(String[] args) {
        CommandRegistry commandRegistry = new CommandRegistry();
        // Logout Commands
        commandRegistry.register(new HelpCommand());
        commandRegistry.register(new RegisterCommand());
        commandRegistry.register(new LoginCommand());
        commandRegistry.register(new QuitCommand());
        // Login Commands
        commandRegistry.register(new LogoutCommand());
        commandRegistry.register(new CreateGameCommand());
        commandRegistry.register(new ListGamesCommand());
        commandRegistry.register(new JoinGameCommand());


        // registers base userState
        UserState userState = new UserState("localhost", 8080, null, null, false, null);

        simplePrint(12, String.format("%sWelcome to Chess! Feel free to sign in, or type 'h' for help.%s\n\n",
                WHITE_KING, WHITE_QUEEN));
        Scanner scanner = new Scanner(System.in);

        while (true) {
            // determines log-in state for command inputs.
            String loginState = userState.isLoggedIn() ? "Logged In" : "Logged Out";
            simplePrint(6, String.format("[%s] >>> ", loginState));

            String line = scanner.nextLine();
            var inputData = line.split(" ");
            String commandName = inputData[0].toLowerCase();
            CommandInterface command = commandRegistry.find(commandName);

            // First check to see if a command is even there.
            if (command == null) {
                simplePrint(1, "Please enter a valid command. Type \"help\" for assistance.\n");
                continue;
            } else if (commandName.equals("quit")) {
                simplePrint(12, "Exiting Chess...\n");
                break;
            }
            // gets hold of the remaining arguments inputted.
            String[] arguments = Arrays.copyOfRange(inputData, 1, inputData.length);
            if (userState.isLoggedIn() || !command.requiresLogin()) {
                ValidationResult validationResult = command.validate(arguments, userState);
                if (!validationResult.ok) {
                    simplePrint(1, validationResult.message + "\n");
                    continue;
                }
                CommandResult commandResult = command.execute(arguments, userState, commandRegistry);
                if (commandResult.ok()) {
                    simplePrint(5, commandResult.message());
                } else {
                    simplePrint(1, commandResult.message() + "\n");
                }
            } else {
                simplePrint(1, "You must be logged in to use that command.\n");
            }
        }
    }


    private static void simplePrint(int colorID, String message) {
        System.out.printf("\u001b[38;5;%dm%s%s", colorID, message, RESET_TEXT_COLOR);
    }
}
