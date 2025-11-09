package client;

import client.commands.CommandInterface;
import client.commands.CommandRegistry;
import client.commands.HelpCommand;
import client.results.CommandResult;
import client.results.ValidationResult;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class ChessClient {

    public static void main(String[] args) {
        // registration of commands
        CommandRegistry commandRegistry = new CommandRegistry();
        commandRegistry.register(new HelpCommand());
        // can add more commands here.

        // registers base userState
        UserState userState = new UserState("localhost", 8080, null, null, false);

        simplePrint(12, String.format("%sWelcome to Chess! Feel free to sign in, or type 'h' for help.%s\n\n",
                WHITE_KING, WHITE_QUEEN));

        while (true) {
            // determines log-in state for command inputs.
            String loginState = userState.isLoggedIn() ? "Logged In" : "Logged Out";
            simplePrint(6, String.format("[%s] >>> ", loginState));

            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            var inputData = line.split(" ");
            String commandName = inputData[0].toLowerCase();
            CommandInterface command = commandRegistry.find(commandName);

            // First check to see if a command is even there.
            if (command == null) {
                simplePrint(1, "Please enter a valid command. Type \"help\" for assistance.\n");
                continue;
            }
            // gets hold of the remaining arguments inputted.
            String[] arguments = Arrays.copyOfRange(inputData, 1, inputData.length);
            ValidationResult validationResult = command.validate(arguments, userState);
            if (!validationResult.ok) {
                simplePrint(1, validationResult.message + " Usage => " + command.getUsage());
                continue;
            }
            CommandResult commandResult = command.execute(args, userState, commandRegistry);
            if (commandResult.ok()) {
                simplePrint(12, commandResult.message());
            }
        }
    }


    private static void simplePrint(int colorID, String message) {
        System.out.printf("\u001b[38;5;%dm%s%s", colorID, message, RESET_TEXT_COLOR);
    }
}
