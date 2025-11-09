package client.commands;

import client.ServerFacade;
import client.UserState;
import client.results.CommandResult;
import client.results.ValidationResult;
import exceptions.InvalidException;

import java.util.List;

public class ObserveCommand implements CommandInterface{

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
    public int getMinArgs() {
        return 1;
    }

    @Override
    public int getMaxArgs() {
        return 1;
    }

    @Override
    public boolean requiresLogin() {
        return true;
    }

    @Override
    public ValidationResult validate(String[] args, UserState userState) {
        // argument length check
        if (args.length == getMinArgs()) {
            return new ValidationResult(true, "");
        }
        return new ValidationResult(false, "Incorrect amount of arguments, expected 1.");
    }

    @Override
    public CommandResult execute(String[] args, UserState userState, CommandRegistry registery) {
        String gameID = args[0];

        try {
            serverFacade.observeGame(gameID, userState.getActiveGames());
            return new CommandResult(true, "");
        } catch (Exception e) {
            return new CommandResult(false, "Error: " + e.getMessage());
        }
    }
}
