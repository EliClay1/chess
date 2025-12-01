package server.websocket;

import chess.ChessGame;
import chess.InvalidMoveException;
import chess.NotUsersTurnException;
import com.google.gson.Gson;
import dataaccess.MySQLDataAccess;
import exceptions.DataAccessException;
import io.javalin.websocket.*;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import websocket.commands.UserGameCommand;
import websocket.commands.UserGameCommand.CommandType.*;
import websocket.messages.ServerMessage;

import java.io.IOException;
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
        System.out.println("Websocket closed.");
    }

    @Override
    public void handleConnect(@NotNull WsConnectContext ctx) {
        System.out.println("Websocket Connected.");
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
        try {
            if (db.getAuth(authToken) == null) {
                throw new Exception("Invalid Authorization.");
            }

            String username = db.getAuth(authToken).username();
            GameData gameData;

            if (db.getGame(gameId) == null) {
                throw new Exception("Invalid GameID");
            }
            gameData = db.getGame(gameId);

            String userType;
            ServerMessage joinNotificationMessage;

            if (Objects.equals(gameData.whiteUsername(), username)) {
                userType = "white";
            } else if (Objects.equals(gameData.blackUsername(), username)) {
                userType = "black";
            } else {
                userType = "observer";
            }

            connections.addSessionToGame(gameId, session);

            // sends game to only the joining user
            ServerMessage loadGameMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameData.game());
            session.getRemote().sendString(serializer.toJson(loadGameMessage));

            // sends join message to all users within the game
            joinNotificationMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    String.format("%s has joined as %s.\n", username, userType));
            for (var sesh : connections.getSessionsForGame(gameId)) {
                if (sesh.isOpen() && sesh != session) {
                    sesh.getRemote().sendString(serializer.toJson(joinNotificationMessage));
                }
            }
        } catch (Exception e) {
            try {
                ServerMessage errorMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR, (String) null);
                errorMessage.setErrorMessage(e.getMessage());
                session.getRemote().sendString(serializer.toJson(errorMessage));
            } catch (Exception e1) {
            }

        }
    }

    private void disconnectUserFromGame(String authToken, int gameId, Session session) {
        try {
            String username = db.getAuth(authToken).username();
            GameData gameData = db.getGame(gameId);
            String userType;

            if (Objects.equals(gameData.whiteUsername(), username)) {
                userType = "white";
            } else if (Objects.equals(gameData.blackUsername(), username)) {
                userType = "black";
            } else {
                userType = "observer";
            }

            connections.removeSessionFromGame(gameId, session);

            // updates the game users
            if (Objects.equals(userType, "white") || Objects.equals(userType, "black")) {
                GameData updatedGameData;
                if (Objects.equals(userType, "white")) {
                    updatedGameData = new GameData(gameData.gameID(), null, gameData.blackUsername(),
                            gameData.gameName(), gameData.game());
                } else {
                    updatedGameData = new GameData(gameData.gameID(), gameData.whiteUsername(), null,
                            gameData.gameName(), gameData.game());
                }
                db.updateGame(updatedGameData);
            }

            ServerMessage leaveGameNotificationMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    String.format("%s has left the game. \n", username));
            for (var sesh : connections.getSessionsForGame(gameId)) {
                if (sesh.isOpen() && sesh != session) {
                    sesh.getRemote().sendString(serializer.toJson(leaveGameNotificationMessage));
                }
            }
        } catch (Exception e) {
        }
    }


    private void makeMove(String authToken, int gameId, List<String> additionalArgs, Session session) {
        // This should have two components in it.
        // TODO -  promotional pieces. Figure out when and how this will work.
        List<String> moveParts = List.of(additionalArgs.getFirst().split(","));


        // TODO - add messages for Check, Checkmate, and Stalemate. Make sure that moves are prevented and the game is completed if this happens.
        // TODO - add message for when the move
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
            } else {
                errorMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR,
                        "An unexpected error occurred: " + e.getMessage());
            }
            try {
                // FIXME - Breaks when attempting to serialize the error message. not exactly sure what could be causing this.
                String jsonError = serializer.toJson(errorMessage);
                session.getRemote().sendString(jsonError);
            } catch (Exception ex) {
                String test = ex.getMessage();
                System.out.print("SERVER ERROR: " + test);
            }
        }
    }


    private void resignUser(String authToken, int gameID, Session ctx) {
        // technically this should simutaneously disconnect the user from the game.
        System.out.print("User has resigned from the game.\n");
    }



}
