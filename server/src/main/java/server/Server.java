package server;

import io.javalin.*;

public class Server {

    private final Javalin javalinServer;


    public Server() {
        Handlers handlers = new Handlers();
        javalinServer = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.

        javalinServer.delete("db", ctx -> ctx.result("{}"));
        // ctx -> register(ctx) ===== this::register
        // TODO: REGISTER
        javalinServer.post("user", handlers::registerHandler);

    }

    public int run(int desiredPort) {
        javalinServer.start(desiredPort);
        return javalinServer.port();
    }

    public void stop() {
        javalinServer.stop();
    }
}
