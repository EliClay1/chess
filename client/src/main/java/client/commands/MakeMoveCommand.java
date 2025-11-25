package client.commands;

import client.ServerFacade;
import client.UserState;
import client.results.CommandResult;
import client.results.ValidationResult;
import client.websocket.NotificationHandler;
import client.websocket.WebsocketFacade;
import exceptions.AlreadyTakenException;
import exceptions.InvalidException;
import websocket.messages.ServerMessage;

import java.util.List;

public class MakeMoveCommand implements CommandInterface{

    private final ServerFacade serverFacade = new ServerFacade();
    private final WebsocketFacade websocketFacade = new WebsocketFacade("localhost:8080");
    private final int argumentCount = 2;

    public MakeMoveCommand() throws Exception {
    }

    @Override
    public String getName() {
        return "move";
    }

    @Override
    public List<String> getAliases() {
        return List.of("m");
    }

    @Override
    public String getUsage() {
        return "Make a move: \"m\", \"move\" <move>\n";
    }

    @Override
    public boolean requiresLogin() {
        return true;
    }

    // TODO - requires

    @Override
    public ValidationResult validate(String[] args, UserState userState) {
        // argument length check
        if (args.length == argumentCount) {
            return new ValidationResult(true, "");
        }
        return new ValidationResult(false, "Incorrect amount of arguments, expected 2.");
    }

    @Override
    public CommandResult execute(String[] args, UserState userState, CommandRegistry registery) {
        String gameID = args[0];
        String teamColor = args[1];

        try {
            serverFacade.joinGame("localhost", 8080, "/game", userState.getAuthToken(), gameID, teamColor);
            websocketFacade.sendMessage("CONNECT");

            return new CommandResult(true, "");
        } catch (Exception e) {
            if (e instanceof InvalidException) {
                return new CommandResult(false, "Invalid team color.");
            } else if (e instanceof NumberFormatException) {
                return new CommandResult(false, "Invalid GameID.");
            } else if (e instanceof AlreadyTakenException) {
                return new CommandResult(false, "That team is already taken.");
            }
            else {
                return new CommandResult(false, "Error: " + e.getMessage());
            }
        }
    }
}
