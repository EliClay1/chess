package client.commands;

import client.ClientState;
import client.ServerFacade;
import client.UserStateData;
import client.commands.command_implementation.BaseCommand;
import client.commands.command_implementation.CommandRegistry;
import client.results.CommandResult;
import exceptions.InvalidException;

import java.util.Collection;
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
    public Collection<ClientState> allowedStates() {
        return List.of(ClientState.LOGGED_IN);
    }

    @Override
    public CommandResult execute(String[] args, UserStateData userStateData, CommandRegistry registery) {
        String gameName = args[0];

        try {
            String gameID = serverFacade.createGame("localhost", 8080, "/game", userStateData.getAuthToken(), gameName);
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
