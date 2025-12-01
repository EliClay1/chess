package client.commands;

import chess.ChessGame;
import client.ClientState;
import client.ServerFacade;
import client.UserStateData;
import client.results.CommandResult;
import client.websocket.NotificationHandler;
import client.websocket.WebsocketFacade;
import com.google.gson.Gson;
import exceptions.AlreadyTakenException;
import exceptions.InvalidException;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.util.Collection;
import java.util.List;

import static ui.EscapeSequences.RESET_TEXT_COLOR;

public class ObserveCommand extends BaseCommand implements NotificationHandler {

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
        return List.of(ClientState.OBSERVING_GAME);
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

    @Override
    public CommandResult execute(String[] args, UserStateData userStateData, CommandRegistry registery) throws Exception {

        String gameId = args[0];

        try {
            WebsocketFacade websocketFacade = new WebsocketFacade(String.format("http://%s:%s", userStateData.getHost(),
                    userStateData.getPort()), serverFacade, this);
            userStateData.setWebsocketFacade(websocketFacade);
            serverFacade.observeGame("localhost", 8080, "/observe", userStateData.getAuthToken(), gameId);
            UserGameCommand connectCommand = new UserGameCommand(UserGameCommand.CommandType.CONNECT, userStateData.getAuthToken(),
                    Integer.parseInt(gameId), "");

            websocketFacade.sendMessage(new Gson().toJson(connectCommand));
            userStateData.setClientState(ClientState.OBSERVING_GAME);

            return new CommandResult(true, "");
        } catch (Exception e) {
            return switch (e) {
                case InvalidException invalidException -> new CommandResult(false, "Invalid team color.");
                case NumberFormatException numberFormatException -> new CommandResult(false, "Invalid GameID.");
                case AlreadyTakenException alreadyTakenException ->
                        new CommandResult(false, "That team is already taken.");
                default -> new CommandResult(false, "Error: " + e.getMessage());
            };
        }
    }

    @Override
    public void notify(ServerMessage serverMessage) {
        if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
            ChessGame chessGame = serverMessage.getGame();
            serverFacade.printBoard(teamColor, chessGame);
        } else if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
            String message = serverMessage.getMessage();
            System.out.printf("\n\u001b[38;5;%dm%s%s", 4, message, RESET_TEXT_COLOR);

            System.out.printf("\u001b[38;5;%dm%s%s", 6, "[Playing] >>> ", RESET_TEXT_COLOR);

        }
    }
}
