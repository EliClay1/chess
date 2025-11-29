package server.websocket;

import chess.ChessGame;
import chess.InvalidMoveException;
import chess.NotUsersTurnException;
import com.google.gson.Gson;
import dataaccess.MySQLDataAccess;
import io.javalin.websocket.*;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import websocket.commands.UserGameCommand;
import websocket.commands.UserGameCommand.CommandType.*;
import websocket.messages.ServerMessage;

import java.util.List;
import java.util.Objects;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final GameService gameService;
    private final MySQLDataAccess db;
    private final Gson serializer = new Gson();
    private final ConnectionManager connections = new ConnectionManager();

    public WebSocketHandler(MySQLDataAccess db) {
        this.gameService = new GameService(db);
        this.db = db;
    }

    @Override
    public void handleClose(@NotNull WsCloseContext wsCloseContext) {
        System.out.println("Websocket closed.\n");
    }

    @Override
    public void handleConnect(@NotNull WsConnectContext ctx) {
        System.out.print("Websocket Connected.\n");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) {

        UserGameCommand command = serializer.fromJson(ctx.message(), UserGameCommand.class);

        int gameId = command.getGameID();
        String authToken = command.getAuthToken();
        var commandType = command.getCommandType();

        if (commandType == UserGameCommand.CommandType.CONNECT) {
            connectUserToGame(authToken, gameId, ctx.session);
        } else if (commandType == UserGameCommand.CommandType.MAKE_MOVE) {
            makeMove(authToken, gameId, command.additionalArguments(), ctx.session);
        } else if (commandType == UserGameCommand.CommandType.LEAVE) {
            disconnectUserFromGame(authToken, gameId, ctx.session);
        } else if (commandType == UserGameCommand.CommandType.RESIGN) {
            resignUser(authToken, gameId, ctx.session);
        }
    }

    private void connectUserToGame(String authToken, int gameId, Session session) {
        connections.addSessionToGame(gameId, session);

        try {
            String user = db.getAuth(authToken).username();
            String userColor = Objects.equals(db.getGame(gameId).whiteUsername(), user) ? "White" : "Black";
            ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    String.format("Successfully joined %s user: %s \n", userColor, user));
            String serializedMessage = serializer.toJson(serverMessage);

            ChessGame chessGame = db.getGame(gameId).game();
            ServerMessage notificationMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, chessGame);
            String serializedGame = serializer.toJson(notificationMessage);

            // Prevents duplicate printing
            session.getRemote().sendString(serializedGame);

            for (var sesh : connections.getSessionsForGame(gameId)) {
                if (sesh.isOpen()) {
                    sesh.getRemote().sendString(serializedMessage);
                }
            }
        } catch (Exception e) {
        }
    }

    private void makeMove(String authToken, int gameId, List<String> additionalArgs, Session session) {
        // This should have two components in it.
        // TODO -  promotional pieces. Figure out when and how this will work.
        List<String> moveParts = List.of(additionalArgs.getFirst().split(","));

        try {
            gameService.makeMove(authToken, moveParts, gameId, null);

            String user = db.getAuth(authToken).username();
            ServerMessage moveMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    String.format("%s has made the move, %s to %s. \n", user, moveParts.getFirst(), moveParts.getLast()));
            String serializedMoveMessage = serializer.toJson(moveMessage);

            ChessGame chessGame = db.getGame(gameId).game();
            ServerMessage notificationMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, chessGame);
            String serializedGame = serializer.toJson(notificationMessage);
            for (var sesh : connections.getSessionsForGame(gameId)) {
                if (sesh.isOpen()) {
                    sesh.getRemote().sendString(serializedGame);
                    sesh.getRemote().sendString(serializedMoveMessage);

                }
            }
        } catch (Exception e) {
            ServerMessage errorMessage = null;
            if (e instanceof InvalidMoveException) {
                errorMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR,
                        String.format("Sorry, the move %s to %s is not valid. Please try again. \n",
                                moveParts.getFirst(), moveParts.getLast()));
            } else if (e instanceof NotUsersTurnException) {
                errorMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage());
            }
            try {
                session.getRemote().sendString(serializer.toJson(errorMessage));
            } catch (Exception ex) {
                System.out.print("SERVER ERROR: " + ex.getMessage());
            }
        }
    }

    private void disconnectUserFromGame(String authToken, int gameId, Session session) {
        connections.removeSessionFromGame(gameId, session);
        System.out.print("User has left the game.\n");


        try {
            String user = db.getAuth(authToken).username();
            ServerMessage serverMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    String.format("%s has left the game. \n", user));
            String serializedMessage = serializer.toJson(serverMessage);

            for (var sesh : connections.getSessionsForGame(gameId)) {
                if (sesh.isOpen()) {
                    sesh.getRemote().sendString(serializedMessage);
                }
            }

        } catch (Exception e) {
            // TODO - remove AFTER testing.
            System.out.printf("Erorr, %s", e.getMessage());
        }

    }

    private void resignUser(String authToken, int gameID, Session ctx) {
        // technically this should simutaneously disconnect the user from the game.
        System.out.print("User has resigned from the game.\n");
    }



}
