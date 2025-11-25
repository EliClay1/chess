package client.websocket;

import com.google.gson.Gson;

import jakarta.websocket.*;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


public class WebsocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;

    public WebsocketFacade(String url) throws Exception {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
//            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler((MessageHandler.Whole<String>) message -> {
                ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
                notificationHandler.notify(notification);
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
}