package client.commands;

import client.ServerFacade;
import client.UserState;
import client.results.CommandResult;
import exceptions.InvalidException;

import java.util.List;

public class CreateGameCommand extends BaseCommand {

    private final ServerFacade serverFacade = new ServerFacade();

    @Override
    public String getName() {
        return "create";
    }

    @Override
    public List<String> getAliases() {
        return List.of("c");
    }

    @Override
    public String getUsage() {
        return "Create a new game: \"c\", \"create\" <GAME NAME>\n";
    }

    @Override
    public boolean requiresLogin() {
        return true;
    }

    @Override
    public CommandResult execute(String[] args, UserState userState, CommandRegistry registery) {
        String gameName = args[0];

        try {
            String gameID = serverFacade.createGame("localhost", 8080, "/game", userState.getAuthToken(), gameName);
            return new CommandResult(true, String.format("Successfully created game: %s, ID: %s\n", gameName, gameID));
        } catch (Exception e) {
            if (e instanceof InvalidException) {
                return new CommandResult(false, "Invalid characters.");
            } else {
                return new CommandResult(false, "Error: " + e.getMessage());
            }
        }
    }
}
