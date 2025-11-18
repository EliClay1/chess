package server.websocket;

import io.javalin.websocket.*;
import org.jetbrains.annotations.NotNull;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    @Override
    public void handleClose(@NotNull WsCloseContext wsCloseContext) throws Exception {
        System.out.println("Websocket closed.");
    }

    @Override
    public void handleConnect(@NotNull WsConnectContext ctx) throws Exception {
        System.out.print("Websocket Connected.");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext wsMessageContext) throws Exception {

    }
}
