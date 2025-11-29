package client.websocket;

import client.ServerFacade;
import com.google.gson.Gson;

import jakarta.websocket.*;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


public class WebsocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;
    ServerFacade serverFacade;

    public WebsocketFacade(String url, ServerFacade facade, NotificationHandler notiHandler) throws Exception {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();

            this.notificationHandler = notiHandler;  // Set it FIRST
            this.serverFacade = facade;

            container.connectToServer(new Endpoint() {
                @Override
                public void onOpen(Session session, EndpointConfig endpointConfig) {
                    WebsocketFacade.this.session = session;

                    session.addMessageHandler(String.class, message -> {
                        ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
                        if (WebsocketFacade.this.notificationHandler != null) {
                            WebsocketFacade.this.notificationHandler.notify(notification);
                        }
                    });
                }
            }, socketURI);
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