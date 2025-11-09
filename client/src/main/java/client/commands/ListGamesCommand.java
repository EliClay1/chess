package client.commands;

import client.ServerFacade;
import client.UserState;
import client.results.CommandResult;
import client.results.ValidationResult;

import java.util.List;
import java.util.Map;

import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_MAGENTA;

public class ListGamesCommand implements CommandInterface{

    private final ServerFacade serverFacade = new ServerFacade();
    private final int argumentCount = 0;

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public List<String> getAliases() {
        return List.of("l");
    }

    @Override
    public String getUsage() {
        return "List current games: \"l\", \"list\"\n";
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
        return new ValidationResult(false, "List games shouldn't have any arguments.");
    }

    @Override
    public CommandResult execute(String[] args, UserState userState, CommandRegistry registery) {
        try {
            var body = serverFacade.listGames("localhost", 8080, "/game", userState.getAuthToken());
            for (Map<String, String> gameData : body) {
                System.out.printf(" %s%s. Game Name: %s, White: %s, Black: %s\n%s", SET_TEXT_COLOR_MAGENTA, gameData.get("gameID"),
                        gameData.get("gameName"), gameData.get("whiteUsername"), gameData.get("blackUsername"), RESET_TEXT_COLOR);
            }
            userState.setActiveGames(body);
            return new CommandResult(true, "");
        } catch (Exception e) {
            return new CommandResult(false, "Error: " + e.getMessage());
        }
    }
}
