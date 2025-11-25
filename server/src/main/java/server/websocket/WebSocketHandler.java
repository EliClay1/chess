package server.websocket;

import com.google.gson.Gson;
import io.javalin.websocket.*;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final Gson serializer = new Gson();

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
        var jsonMessage = serializer.fromJson(ctx.message(), Map.class);
        System.out.print("testing");
    }

    // TODO - This handles pretty much everything that the base server handler does, It just

}
