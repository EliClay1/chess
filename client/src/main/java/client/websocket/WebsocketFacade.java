package client.websocket;

import chess.ChessGame;
import client.ServerFacade;
import com.google.gson.Gson;

import jakarta.websocket.*;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static ui.EscapeSequences.RESET_TEXT_COLOR;


public class WebsocketFacade extends Endpoint implements AutoCloseable {

    Session session;
    NotificationHandler notificationHandler;
    ServerFacade serverFacade;

    public WebsocketFacade(String url, ServerFacade facade) throws Exception {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
//            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);
            this.serverFacade = facade;

            //set message handler
            this.session.addMessageHandler((MessageHandler.Whole<String>) message -> {
                ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
                notificationHandler.notify(notification);

                if (notification.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
                    ChessGame game = notification.getChessGame();
                    // TODO - figure out how you're going to get hold of the color.
                    serverFacade.printBoard("white", game);
                    String printMessage = notification.getMessage();
                    // TODO - might want to change message color.
                    System.out.printf("\u001b[38;5;%dm%s%s", 5, printMessage, RESET_TEXT_COLOR);
                }
            });
        } catch (URISyntaxException | DeploymentException | IOException | IllegalStateException e) {
            throw new Exception(e);
        }

    }

    // required. no external purpose.
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }

    public void sendMessage(String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            System.out.print("Failed to send message, " + e.getMessage());
        }
    }

    @Override
    public void close() throws Exception {

    }
}