package client.commands;

import client.ClientState;
import client.ServerFacade;
import client.UserStateData;
import client.results.CommandResult;
import exceptions.InvalidException;

import java.util.Collection;
import java.util.List;

public class ObserveCommand extends BaseCommand {

    private final ServerFacade serverFacade = new ServerFacade();

    @Override
    public String getName() {
        return "observe";
    }

    @Override
    public List<String> getAliases() {
        return List.of("o");
    }

    @Override
    public String getUsage() {
        return "Observe a game: \"o\", \"observe\" <GAME ID>\n";
    }

    @Override
    public Collection<ClientState> allowedStates() {
        return List.of(ClientState.LOGGED_IN, ClientState.OBSERVING_GAME);
    }

    @Override
    public CommandResult execute(String[] args, UserStateData userStateData, CommandRegistry registery) {
        String gameID = args[0];

        try {
            serverFacade.observeGame(gameID, userStateData.getActiveGames());
            return new CommandResult(true, "");
        } catch (Exception e) {
            if (e instanceof InvalidException) {
                return new CommandResult(false, "Please print out a list of games before attempting to observe. (l)");
            }
            return new CommandResult(false, "Error: " + e.getMessage());
        }
    }
}
