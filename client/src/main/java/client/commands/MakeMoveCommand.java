package client.commands;

import client.ClientState;
import client.ServerFacade;
import client.UserStateData;
import client.results.CommandResult;
import client.results.ValidationResult;
import client.websocket.WebsocketFacade;
import com.google.gson.Gson;
import exceptions.AlreadyTakenException;
import exceptions.InvalidException;
import websocket.commands.UserGameCommand;

import java.util.Collection;
import java.util.List;

public class MakeMoveCommand implements CommandInterface{

    private final ServerFacade serverFacade = new ServerFacade();
    private final WebsocketFacade websocketFacade = new WebsocketFacade("http://localhost:8080");
    private final int argumentCount = 1;

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
    public Collection<ClientState> allowedStates() {
        return List.of(ClientState.LOGGED_IN, ClientState.PLAYING_GAME);
    }

    // TODO - requires

    @Override
    public ValidationResult validate(String[] args, UserStateData userStateData) {
        // argument length check
        if (args.length == argumentCount) {
            return new ValidationResult(true, "");
        }
        return new ValidationResult(false, String.format("Incorrect amount of arguments, expected %d.", argumentCount));
    }

    @Override
    public CommandResult execute(String[] args, UserStateData userStateData, CommandRegistry registery) {
        String gameID = args[0];
        String teamColor = args[1];

        try {
            serverFacade.joinGame(userStateData.getHost(), userStateData.getPort(), "/game",
                    userStateData.getAuthToken(), gameID, teamColor);
            UserGameCommand moveCommand = new UserGameCommand(UserGameCommand.CommandType.CONNECT, userStateData.getAuthToken(),
                    Integer.parseInt(gameID), "");
            websocketFacade.sendMessage(new Gson().toJson(moveCommand));
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
