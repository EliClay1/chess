package server.websocket;

import com.google.gson.Gson;
import io.javalin.websocket.*;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import websocket.commands.UserGameCommand;
import websocket.commands.UserGameCommand.CommandType.*;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final Gson serializer = new Gson();
    private final ConnectionManager connections = new ConnectionManager();

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
            connectUserToGame(authToken, gameId, ctx.session);
        } else if (commandType == UserGameCommand.CommandType.MAKE_MOVE) {
            makeMove(authToken, gameId);
        } else if (commandType == UserGameCommand.CommandType.LEAVE) {
            disconnectUserFromGame(authToken, gameId, ctx.session);
        } else if (commandType == UserGameCommand.CommandType.RESIGN) {
            resignUser(authToken, gameId);
        } else {
            // TODO - replace with actual error handling.
            System.out.print("Error, Something went wrong.");
        }
    }

    // TODO - This handles pretty much everything that the base server handler does, It just


    // TODO - questionable naming convention, minor POLA. Is it connecting to the ws? the game? Better name would be good for this.

    private void connectUserToGame(String authToken, int gameID, Session session) {
        connections.addSession(session);
        System.out.print("Successfully Joined User.\n");
    }

    private void disconnectUserFromGame(String authToken, int gameID, Session session) {
        connections.removeSession(session);
        System.out.print("User has left the game.\n");


    }

    private void makeMove(String authToken, int gameID) {
        // Find some way to transfer over the game move choice.
        System.out.print("User has made a move.\n");
    }

    private void resignUser(String authToken, int gameID) {
        // technically this should simutaneously disconnect the user from the game.
        System.out.print("User has resigned from the game.\n");
    }



}
