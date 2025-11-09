package client.commands;

import client.ServerFacade;
import client.UserState;
import client.results.CommandResult;
import client.results.ValidationResult;

import java.util.List;

public class CreateGameCommand implements CommandInterface{

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
        String gameName = args[0];

        try {
            serverFacade.createGame("localhost", 8080, "/game", userState.getAuthToken(), gameName);
            return new CommandResult(true, String.format("Successfully created game: %s\n", gameName));
        } catch (Exception e) {
            return new CommandResult(false, "Error: " + e.getMessage());
        }
    }
}
