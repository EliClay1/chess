package client;

import client.commands.*;
import client.commands.implementation.CommandInterface;
import client.commands.implementation.CommandRegistry;
import client.results.CommandResult;
import client.results.ValidationResult;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class ChessClient {

    public void run() throws Exception {
        CommandRegistry commandRegistry = new CommandRegistry();
        try {
            // Logout Commands
            commandRegistry.register(new HelpCommand());
            commandRegistry.register(new RegisterCommand());
            commandRegistry.register(new LoginCommand());
            commandRegistry.register(new QuitCommand());
            // Login Commands
            commandRegistry.register(new LogoutCommand());
            commandRegistry.register(new CreateGameCommand());
            commandRegistry.register(new ListGamesCommand());

            // WebSocket Commands
            commandRegistry.register(new JoinGameCommand());
            commandRegistry.register(new ObserveCommand());
            commandRegistry.register(new MakeMoveCommand());
            commandRegistry.register(new LeaveGameCommand());
            commandRegistry.register(new ResignCommand());
            commandRegistry.register(new RedrawBoardCommand());
            commandRegistry.register(new HighlightMovesCommand());

            // TODO - Highlight Moves, Re-draw Board,

        } catch (Exception e) {
            simplePrint(1, e.getMessage());
        }


        // FIXME - change how the commands interpret the userStateData and allow them to pull directly from it.
        //  ZERO HARD CODED VALUES
        // registers base userStateData
        UserStateData userStateData = new UserStateData("localhost", 8080, null,
                null, ClientState.LOGGED_OUT, null, 0, null, null);

        simplePrint(12, String.format("%sWelcome to Chess! Feel free to sign in, or type 'h' for help.%s\n\n",
                WHITE_KING, WHITE_QUEEN));
        Scanner scanner = new Scanner(System.in);

        while (true) {
            String statePrintValue;

            switch (userStateData.clientState()) {
                case LOGGED_OUT -> statePrintValue = "Logged Out";
                case LOGGED_IN -> statePrintValue = "Logged In";
                case PLAYING_GAME -> statePrintValue = "Playing";
                case OBSERVING_GAME -> statePrintValue = "Observing";
                default -> throw new IllegalStateException("Unexpected value: " + userStateData.clientState());
            }

            // Prevents the Playing state from printing. It's handled on the command level.
            // That may be a coupling issue or something, but honestly I don't care.
            if (userStateData.clientState() == ClientState.LOGGED_OUT ||
                    userStateData.clientState() == ClientState.LOGGED_IN) {
                simplePrint(6, String.format("[%s] >>> ", statePrintValue));
            }

            String line = scanner.nextLine();
            var inputData = line.split(" ");
            String commandName = inputData[0].toLowerCase();
            CommandInterface command = commandRegistry.find(commandName);

            // First check to see if a command is even there.
            if (command == null) {
                simplePrint(1, "Please enter a valid command. Type \"help\" for assistance.\n");
                printAdditionalCommandUI(userStateData);
                continue;
            }
            // gets hold of the remaining arguments inputted.
            String[] arguments = Arrays.copyOfRange(inputData, 1, inputData.length);
            if (command.allowedStates().contains(userStateData.clientState())) {
                ValidationResult validationResult = command.validate(arguments, userStateData);
                if (!validationResult.ok) {
                    simplePrint(1, validationResult.message + "\n");
                    printAdditionalCommandUI(userStateData);
                    continue;
                }
                // check for quit command
                if (commandName.equals("quit")) {
                    simplePrint(12, "Exiting Chess...\n");
                    break;
                }

                CommandResult commandResult = command.execute(arguments, userStateData, commandRegistry);
                if (commandResult == null) {
                    simplePrint(1, "failed command.\n");
                    continue;
                }
                if (commandResult.ok()) {
                    simplePrint(5, commandResult.message());
                } else {
                    simplePrint(1, commandResult.message() + "\n");
                }
            } else {
                simplePrint(1, "You must be logged in or playing a game to use that command.\n");
                printAdditionalCommandUI(userStateData);
            }
        }
    }

    public static void printAdditionalCommandUI(UserStateData userStateData) {
        if (userStateData.clientState() == ClientState.OBSERVING_GAME) {
            simplePrint(6, "[Observing] >>> ");
        } else if (userStateData.clientState() == ClientState.PLAYING_GAME) {
            simplePrint(6, "[Playing] >>> ");
        }
    }


    private static void simplePrint(int colorID, String message) {
        System.out.printf("\u001b[38;5;%dm%s%s", colorID, message, RESET_TEXT_COLOR);
    }
}
