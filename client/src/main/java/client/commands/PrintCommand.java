package client.commands;

import chess.ChessGame;
import client.ChessClient;
import client.ClientState;
import client.ServerFacade;
import client.UserStateData;
import client.commands.implementation.BaseCommand;
import client.commands.implementation.CommandRegistry;
import client.results.CommandResult;
import client.results.ValidationResult;
import client.websocket.WebsocketFacade;
import com.google.gson.Gson;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.util.Collection;
import java.util.List;

public class PrintCommand extends BaseCommand {

    public final int argumentCount = 0;
    private final ServerFacade serverFacade = new ServerFacade();
    private UserStateData userStateData;
    private WebsocketFacade websocketFacade;

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getUsage() {
        return "\n";
    }

    @Override
    public Collection<ClientState> allowedStates() {
        return List.of(ClientState.PLAYING_GAME);
    }

    @Override
    public ValidationResult validate(String[] args, UserStateData userStateData) {
        if (args.length == argumentCount) {
            return new ValidationResult(true, "");
        }
        return new ValidationResult(false, String.format("Incorrect amount of arguments, expected %d.", argumentCount));
    }

    @Override
    public CommandResult execute(String[] args, UserStateData userState, CommandRegistry registery) throws Exception {
        this.userStateData = userState;

        try {
            websocketFacade = userStateData.getWebsocketFacade();
            websocketFacade.setNotificationHandler(this);
            UserGameCommand drawBoardCommand = new UserGameCommand(UserGameCommand.CommandType.PRINT_BOARD,
                    userStateData.getAuthToken(), userStateData.getActiveGameId(), null);
            websocketFacade.sendMessage(new Gson().toJson(drawBoardCommand));
            return new CommandResult(true, "");
        } catch (Exception e) {
            return new CommandResult(false, "Error: " + e.getMessage());
        }
    }

    @Override
    public void notify(ServerMessage serverMessage) {
        if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
            ChessGame chessGame = serverMessage.getGame();
            if (userStateData.clientState() == ClientState.PLAYING_GAME) {
                serverFacade.printBoard(userStateData.getActiveTeamColor(), chessGame, null);
            } else {
                serverFacade.printBoard("white", chessGame, null);
            }
        } else {
            BaseCommand.notifyMethod(serverMessage);
        }
        ChessClient.printAdditionalCommandUI(userStateData);
    }
}
