package client.commands;

import chess.ChessGame;
import client.ChessClient;
import client.ClientState;
import client.ServerFacade;
import client.UserStateData;
import client.results.CommandResult;
import client.results.ValidationResult;
import client.websocket.NotificationHandler;
import client.websocket.WebsocketFacade;
import com.google.gson.Gson;
import exceptions.InvalidException;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.util.Collection;
import java.util.List;

import static ui.EscapeSequences.RESET_TEXT_COLOR;

public class ObserveCommand extends BaseCommand implements NotificationHandler {

    private final ServerFacade serverFacade = new ServerFacade();
    private WebsocketFacade websocketFacade;
    private final int argumentCount = 1;
    private UserStateData userStateData;

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
        return List.of(ClientState.LOGGED_IN);
    }

    @Override
    public ValidationResult validate(String[] args, UserStateData userStateData) {
        if (args.length == argumentCount) {
            return new ValidationResult(true, "");
        }
        return new ValidationResult(false, String.format("Incorrect amount of arguments, expected %d.", argumentCount));
    }

    @Override
    public CommandResult execute(String[] args, UserStateData userState, CommandRegistry registery) {
        String gameId = args[0];
        this.userStateData = userState;

        try {
            serverFacade.observeGame(gameId, userStateData.getActiveGames());
            UserGameCommand connectCommand = new UserGameCommand(UserGameCommand.CommandType.CONNECT, userStateData.getAuthToken(),
                    Integer.parseInt(gameId), null);
            WebsocketFacade websocketFacade = new WebsocketFacade(String.format("http://%s:%s", userStateData.getHost(),
                    userStateData.getPort()), serverFacade, this);
            userStateData.setWebsocketFacade(websocketFacade);

            websocketFacade.sendMessage(new Gson().toJson(connectCommand));
            userStateData.setClientState(ClientState.OBSERVING_GAME);
            userStateData.setActiveGameId(Integer.parseInt(gameId));

            return new CommandResult(true, "");

        } catch (Exception e) {
            if (e instanceof InvalidException) {
                return new CommandResult(false, "Please print out a list of games before attempting to observe. (l)");
            }
            return new CommandResult(false, "Error: " + e.getMessage());
        }
    }

    @Override
    public void notify(ServerMessage serverMessage) {
        if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
            ChessGame chessGame = serverMessage.getGame();
            serverFacade.printBoard("white", chessGame);
        } else WebsocketCommand.notifyMethod(serverMessage);
        ChessClient.printAdditionalCommandUI(userStateData);
    }
}
