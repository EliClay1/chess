package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
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
            makeMove(authToken, gameId, command.getChessMove(), ctx.session);
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


    private void makeMove(String authToken, int gameId, ChessMove move, Session session) {

        // TODO - check if the correct user is in the game, right players turn, etc, here within the handler.

        // TODO - add messages for Check, Checkmate, and Stalemate. Make sure that moves are prevented and the game is completed if this happens.


        try {
            // TODO - check for authentication
            if (db.getAuth(authToken) == null) {
                throw new Exception("Invalid Authorization");
            }
            String user = db.getAuth(authToken).username();
            GameData gameData = db.getGame(gameId);
            ChessGame.TeamColor playerColor;

            if (Objects.equals(gameData.blackUsername(), user)) {
                playerColor = ChessGame.TeamColor.BLACK;
            } else if (Objects.equals(gameData.whiteUsername(), user)) {
                playerColor = ChessGame.TeamColor.WHITE;
            } else {
                throw new Exception("Observers cannot make moves!");
            }

            if (playerColor != gameData.game().getTeamTurn()) {
                throw new NotUsersTurnException();
            }

            gameData.game().makeMove(move);
            db.updateGame(gameData);

            ServerMessage moveMessage;

            // TODO - The inverse may be needed for the team turn, not entirely sure yet.
            if (gameData.game().isInCheck(gameData.game().getTeamTurn())) {
                // send notification that game is in check
                moveMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                        // TODO - may need to change getTeamTurn string to be lowercase or something.
                        String.format("%s (%s) has made the move, %s to %s.\nThe game is in now in check.\n", user,
                                gameData.game().getTeamTurn().toString(), move.getStartPosition(), move.getEndPosition()));
            } else if (gameData.game().isInCheckmate(gameData.game().getTeamTurn())) {
                // send notification that the game is over, run end game functionality.
                moveMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                        // TODO - may need to change getTeamTurn string to be lowercase or something.
                        String.format("%s (%s) has made the move, %s to %s.\nCheckmate.\n", user,
                                gameData.game().getTeamTurn().toString(), move.getStartPosition(), move.getEndPosition()));
            } else if (gameData.game().isInStalemate(gameData.game().getTeamTurn())) {
                // send notification that game is at a stalemate, run end game functionality
                moveMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                        // TODO - may need to change getTeamTurn string to be lowercase or something.
                        String.format("%s (%s) has made the move, %s to %s.\nThe game has entered into a stalemate.\n", user,
                                gameData.game().getTeamTurn().toString(), move.getStartPosition(), move.getEndPosition()));
            } else {
                // send default notification for chess move.
                moveMessage = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                        // TODO - may need to change getTeamTurn string to be lowercase or something.
                        String.format("%s (%s) has made the move, %s to %s. \n", user,
                                gameData.game().getTeamTurn().toString(), move.getStartPosition(), move.getEndPosition()));
            }

            ServerMessage updateGameMessage = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameData.game());
            session.getRemote().sendString(serializer.toJson(updateGameMessage));

            for (var sesh : connections.getSessionsForGame(gameId)) {
                if (sesh.isOpen() && sesh != session) {
                    sesh.getRemote().sendString(serializer.toJson(updateGameMessage));
                    sesh.getRemote().sendString(serializer.toJson(moveMessage));

                }
            }
        } catch (Exception e) {
            ServerMessage errorMessage = getErrorMessage(move, e);
            try {
                session.getRemote().sendString(serializer.toJson(errorMessage));
            } catch (Exception ex) {
            }
        }
    }

    @NotNull
    private static ServerMessage getErrorMessage(ChessMove move, Exception e) {
        ServerMessage errorMessage;
        if (e instanceof InvalidMoveException) {
            errorMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR, (String) null);
            errorMessage.setErrorMessage(String.format("Sorry, the move %s to %s is not valid. Please try again.\n",
                    move.getStartPosition(), move.getEndPosition()));
        } else if (e instanceof NotUsersTurnException) {
            errorMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR, (String) null);
            errorMessage.setErrorMessage("It's not your turn.\n");
        } else {
            errorMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR, (String) null);
            errorMessage.setErrorMessage(String.format("%s\n", e.getMessage()));
        }
        return errorMessage;
    }


    private void resignUser(String authToken, int gameID, Session ctx) {
        // technically this should simutaneously disconnect the user from the game.
        System.out.print("User has resigned from the game.\n");
    }



}
