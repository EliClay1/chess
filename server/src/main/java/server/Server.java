package server;

import io.javalin.*;
import io.javalin.http.Context;

public class Server {

    private final Javalin javalinServer;

    public Server() {
        javalinServer = Javalin.create(config -> config.staticFiles.add("web"));

        // TODO - see if there is a way to fix this violation of the geneva convention.
        Handlers handlers = null;
        try {
            handlers = new Handlers();
        } catch (Exception e) {
            javalinServer.error(500, "Database failed to build.", this::failureHandler);
        }
        // ensures that the handlers variable will never = null, prevents additional warnings.
        assert handlers != null;
        javalinServer.delete("db", handlers::clearHandler);
        javalinServer.post("user", handlers::registerHandler);
        javalinServer.post("session", handlers::loginHandler);
        javalinServer.delete("session", handlers::logoutHandler);
        javalinServer.post("game", handlers::createGameHandler);
        javalinServer.put("game", handlers::joinGameHandler);
        javalinServer.get("game", handlers::listGamesHandler);
    }

    // TODO - this is breaking coupling / probably like 5 other design principles. Find a way to fix it.
    private void failureHandler(Context ctx) {
        ctx.status(500);
    }

    public int run(int desiredPort) {
        javalinServer.start(desiredPort);
        return javalinServer.port();
    }

    public void stop() {
        javalinServer.stop();
    }
}
