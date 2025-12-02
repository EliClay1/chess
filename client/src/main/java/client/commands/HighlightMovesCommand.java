package client.commands;

import chess.ChessGame;
import chess.ChessPosition;
import client.ChessClient;
import client.ServerFacade;
import client.UserStateData;
import client.commands.command_implementation.BaseCommand;
import client.commands.command_implementation.CommandRegistry;
import client.results.CommandResult;
import client.results.ValidationResult;
import client.websocket.WebsocketFacade;
import com.google.gson.Gson;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.util.List;

public class HighlightMovesCommand extends PrintCommand {

    public final int argumentCount = 1;
    private final ServerFacade serverFacade = new ServerFacade();
    private UserStateData userStateData;
    private WebsocketFacade websocketFacade;
    ChessPosition position;

    @Override
    public String getName() {
        return "highlight";
    }

    @Override
    public List<String> getAliases() {
        return List.of("hm");
    }

    @Override
    public String getUsage() {
        return "Highlights available moves for any given position: \"hm\", \"highlight\", <move>\n";
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

        String checkPosition = args[0];
        String input = checkPosition.trim().toLowerCase();
        char yChar = input.charAt(0);
        char xChar = input.charAt(1);
        int yInt = (yChar - 'a') + 1;
        int xInt = (xChar - '1') + 1;

        position = new ChessPosition(xInt, yInt);

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
            serverFacade.printBoard(userStateData.getActiveTeamColor(), chessGame, position);
        } else {
            BaseCommand.notifyMethod(serverMessage);
        }
        ChessClient.printAdditionalCommandUI(userStateData);
    }
}
