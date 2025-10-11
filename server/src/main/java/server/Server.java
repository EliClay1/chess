package server;

import com.google.gson.Gson;
import io.javalin.*;
import io.javalin.http.Context;

import java.util.Map;
import java.util.UUID;

public class Server {

    private final Javalin javalinServer;
    private Handlers handlers;


    public Server() {
        handlers = new Handlers();
        javalinServer = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.

        javalinServer.delete("db", ctx -> ctx.result("{}"));
        // ctx -> register(ctx) ===== this::register
        javalinServer.post("user", ctx -> handlers.register(ctx));
    }

    public int run(int desiredPort) {
        javalinServer.start(desiredPort);
        return javalinServer.port();
    }

    public void stop() {
        javalinServer.stop();
    }
}
