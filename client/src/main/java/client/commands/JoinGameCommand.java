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

public class JoinGameCommand implements CommandInterface{

    private final ServerFacade serverFacade = new ServerFacade();

    private final int argumentCount = 2;

    public JoinGameCommand() throws Exception {
    }

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
    public Collection<ClientState> allowedStates() {
        return List.of(ClientState.LOGGED_IN);
    }

    @Override
    public ValidationResult validate(String[] args, UserStateData userStateData) {
        // argument length check
        if (args.length == argumentCount) {
            return new ValidationResult(true, "");
        }
        return new ValidationResult(false, "Incorrect amount of arguments, expected 2.");
    }

    @Override
    public CommandResult execute(String[] args, UserStateData userStateData, CommandRegistry registery) throws Exception {
        try (WebsocketFacade websocketFacade = new WebsocketFacade(String.format("http://%s:%s", userStateData.getHost(), userStateData.getPort()))) {
            String gameID = args[0];
            String teamColor = args[1];

            try {
                serverFacade.joinGame("localhost", 8080, "/game", userStateData.getAuthToken(), gameID, teamColor);
                UserGameCommand connectCommand = new UserGameCommand(UserGameCommand.CommandType.CONNECT, userStateData.getAuthToken(),
                        Integer.parseInt(gameID));
                websocketFacade.sendMessage(new Gson().toJson(connectCommand));
                userStateData.setClientState(ClientState.PLAYING_GAME);

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
        } catch (Exception e) {
            System.out.print("Critical Failure - Couldn't connect to websocket.\n");
        }
        return null;
    }
}
