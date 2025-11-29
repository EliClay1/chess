package server.websocket;

import com.google.gson.Gson;
import dataaccess.MySQLDataAccess;
import exceptions.DataAccessException;
import io.javalin.websocket.*;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import service.UserService;
import websocket.commands.UserGameCommand;
import websocket.commands.UserGameCommand.CommandType.*;

import java.util.List;
import java.util.Objects;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final GameService gameService;
    private final UserService userService;
    private final MySQLDataAccess db;
    private final Gson serializer = new Gson();
    private final ConnectionManager connections = new ConnectionManager();

    public WebSocketHandler(MySQLDataAccess db) {
        this.gameService = new GameService(db);
        this.userService = new UserService(db);
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
        // Need to check if a specific command has gone through

        UserGameCommand command = serializer.fromJson(ctx.message(), UserGameCommand.class);

        // TODO do I need to check for null?

        int gameId = command.getGameID();
        String authToken = command.getAuthToken();
        var commandType = command.getCommandType();

        if (commandType == UserGameCommand.CommandType.CONNECT) {
            connectUserToGame(authToken, gameId, ctx.session, ctx);
        } else if (commandType == UserGameCommand.CommandType.MAKE_MOVE) {
            makeMove(authToken, gameId, command.additionalArguments(), ctx);
            ctx.send("This is a test");
        } else if (commandType == UserGameCommand.CommandType.LEAVE) {
            disconnectUserFromGame(authToken, gameId, ctx.session, ctx);
        } else if (commandType == UserGameCommand.CommandType.RESIGN) {
            resignUser(authToken, gameId, ctx);
        } else {
            // TODO - replace with actual error handling.
            System.out.print("Error, Something went wrong.");
        }
    }

    private void connectUserToGame(String authToken, int gameID, Session session, WsMessageContext ctx) {
        connections.addSession(session);
        try {
            var user = db.getAuth(authToken).username();
            var userColor = Objects.equals(db.getGame(gameID).whiteUsername(), user) ? "White" : "Black";
            ctx.send(String.format("Successfully joined %s user: %s \n", userColor, user));
        } catch (DataAccessException e) {
            System.out.printf("Erorr, %s", e.getMessage());
        }
    }

    private void disconnectUserFromGame(String authToken, int gameID, Session session, WsMessageContext ctx) {
        connections.removeSession(session);
        System.out.print("User has left the game.\n");

    }

    private void makeMove(String authToken, int gameID, List<String> additionalArgs, WsMessageContext ctx) {
        // Find some way to transfer over the game move choice.
        System.out.print("User has made a move.\n");

        // This should have two components in it.
        // TODO -  promotional pieces. Figure out when and how this will work.
        List<String> moveParts = List.of(additionalArgs.getFirst().split(","));


        // TODO - check and make sure that both players are in the game
        try {
            gameService.makeMove(authToken, moveParts, gameID, null);
        } catch (Exception e) {
            System.out.printf("Make move failed, %s", e.getMessage());
        }
    }

    private void resignUser(String authToken, int gameID, WsMessageContext ctx) {
        // technically this should simutaneously disconnect the user from the game.
        System.out.print("User has resigned from the game.\n");
    }



}
