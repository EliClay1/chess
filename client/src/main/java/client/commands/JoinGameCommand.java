package client.commands;

import client.ServerFacade;
import client.UserState;
import client.results.CommandResult;
import client.results.ValidationResult;
import exceptions.AlreadyTakenException;
import exceptions.InvalidException;

import java.util.List;

public class JoinGameCommand implements CommandInterface{

    private final ServerFacade serverFacade = new ServerFacade();
    private final int argumentCount = 2;

    @Override
    public String getName() {
        return "join";
    }

    @Override
    public List<String> getAliases() {
        return List.of("j");
    }

    @Override
    public String getUsage() {
        return "Join an existing game: \"j\", \"join\" <GAME ID> <COLOR>\n";
    }

    @Override
    public boolean requiresLogin() {
        return true;
    }

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
