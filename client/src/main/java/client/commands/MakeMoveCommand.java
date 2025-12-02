package client.commands;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import client.ChessClient;
import client.ClientState;
import client.ServerFacade;
import client.UserStateData;
import client.commands.implementation.BaseCommand;
import client.commands.implementation.CommandInterface;
import client.commands.implementation.CommandRegistry;
import client.results.CommandResult;
import client.results.ValidationResult;
import client.websocket.NotificationHandler;
import client.websocket.WebsocketFacade;
import com.google.gson.Gson;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MakeMoveCommand implements CommandInterface, NotificationHandler {

    private final ServerFacade serverFacade = new ServerFacade();
    private WebsocketFacade websocketFacade;
    private final int argumentCount = 1;
    private UserStateData userStateData;

    public MakeMoveCommand() {
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
        return "Make a move: \"m\", \"move\" <move1,move2,promo piece>\n";
    }

    @Override
    public Collection<ClientState> allowedStates() {
        return List.of(ClientState.PLAYING_GAME);
    }

    @Override
    public ValidationResult validate(String[] args, UserStateData userStateData) {
        // argument length check
        if (args.length == argumentCount) {
            return new ValidationResult(true, "");
        }
        return new ValidationResult(false, String.format("Incorrect amount of arguments, expected %d.", argumentCount));
    }

    @Override
    public CommandResult execute(String[] args, UserStateData userState, CommandRegistry registery) {
        this.userStateData = userState;

        String[] moveParts = args[0].split(",");
        List<ChessPosition> positions = new ArrayList<>(List.of());
        ChessPiece.PieceType promotionalPiece = null;
        if (moveParts.length > 2) {
            switch (moveParts[2].toLowerCase()) {
                case "queen" -> promotionalPiece = ChessPiece.PieceType.QUEEN;
                case "rook" -> promotionalPiece = ChessPiece.PieceType.ROOK;
                case "knight" -> promotionalPiece = ChessPiece.PieceType.KNIGHT;
                case "bishop" -> promotionalPiece = ChessPiece.PieceType.BISHOP;
            }
        }
        int index = 0;
        while (index <= 2) {
            for (var position : moveParts) {
                String input = position.trim().toLowerCase();
                char yChar = input.charAt(0);
                char xChar = input.charAt(1);
                int yInt = (yChar - 'a') + 1;
                int xInt = (xChar - '1') + 1;
                positions.add(new ChessPosition(xInt, yInt));
                index++;
            }
        }
        ChessMove chessMove = new ChessMove(positions.getFirst(), positions.getLast(), promotionalPiece);

        try {
            websocketFacade = userStateData.getWebsocketFacade();
            websocketFacade.setNotificationHandler(this);
            UserGameCommand moveCommand = new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, userStateData.getAuthToken(),
                    userStateData.getActiveGameId(), chessMove);
            websocketFacade.sendMessage(new Gson().toJson(moveCommand));

            return new CommandResult(true, "");
        } catch (Exception e) {
            if (e instanceof NumberFormatException) {
                return new CommandResult(false, "Invalid GameID.");
            } else {
                return new CommandResult(false, "Error: " + e.getMessage());
            }
        }
    }

    @Override
    public void notify(ServerMessage serverMessage) {
        if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
            ChessGame chessGame = serverMessage.getGame();
            serverFacade.printBoard(userStateData.getActiveTeamColor(), chessGame, null);
        } else {
            BaseCommand.notifyMethod(serverMessage);
        }
        ChessClient.printAdditionalCommandUI(userStateData);
    }
}
