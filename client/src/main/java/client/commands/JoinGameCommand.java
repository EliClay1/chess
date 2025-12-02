package client.commands;

import chess.ChessGame;
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
import websocket.messages.ServerMessage;

import java.util.Collection;
import java.util.List;

import static ui.EscapeSequences.RESET_TEXT_COLOR;

public class JoinGameCommand extends WebsocketCommand {

    private final ServerFacade serverFacade = new ServerFacade();
    private WebsocketFacade websocketFacade;
    private final int argumentCount = 2;
    private UserStateData userStateData;

    public JoinGameCommand() {
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
        if (args.length == argumentCount) {
            return new ValidationResult(true, "");
        }
        return new ValidationResult(false, "Incorrect amount of arguments, expected 2.");
    }

    @Override
    public CommandResult execute(String[] args, UserStateData userState, CommandRegistry registery) throws Exception {
        this.userStateData = userState;
        String gameId = args[0];
        String teamColor = args[1];

        try {
            WebsocketFacade websocketFacade = new WebsocketFacade(String.format("http://%s:%s", userStateData.getHost(),
                    userStateData.getPort()), serverFacade, this);
            userStateData.setWebsocketFacade(websocketFacade);
            serverFacade.joinGame("localhost", 8080, "/game", userStateData.getAuthToken(),
                    gameId, teamColor);
            UserGameCommand connectCommand = new UserGameCommand(UserGameCommand.CommandType.CONNECT,
                    userStateData.getAuthToken(), Integer.parseInt(gameId), null);

            userStateData.setClientState(ClientState.PLAYING_GAME);
            userStateData.setActiveGameId(Integer.parseInt(gameId));
            userStateData.setActiveTeamColor(teamColor);

            websocketFacade.sendMessage(new Gson().toJson(connectCommand));


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
            serverFacade.printBoard(userStateData.getActiveTeamColor(), chessGame);
        } else {
            WebsocketCommand.notifyMethod(serverMessage);
        }
        System.out.printf("\u001b[38;5;%dm%s%s", 6, "[Playing] >>> ", RESET_TEXT_COLOR);
    }
}
