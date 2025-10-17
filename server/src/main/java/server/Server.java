package server;

import io.javalin.*;

public class Server {

    private final Javalin javalinServer;


    public Server() {
        Handlers handlers = new Handlers();
        javalinServer = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.

        javalinServer.delete("db", handlers::clearHandler);
        // ctx -> register(ctx) ===== this::register
        // TODO: REGISTER
        javalinServer.post("user", handlers::registerHandler);
        // TODO - LOGIN
        javalinServer.post("session", handlers::loginHandler);

    }

    public int run(int desiredPort) {
        javalinServer.start(desiredPort);
        return javalinServer.port();
    }

    public void stop() {
        javalinServer.stop();
    }
}
