package server;

import io.javalin.*;
import io.javalin.http.Context;

public class Server {

    private final Javalin javalinServer;

    public Server() {
        javalinServer = Javalin.create(config -> config.staticFiles.add("web"));

        try {
            // Handlers must be created with the Handlers.create...() function because of saftey precautions. i.e, you
            // cannot create the handler any other way.
            Handlers handlers = Handlers.createHandlersWithDatabase();
            javalinServer.delete("db", handlers::clearHandler);
            javalinServer.post("user", handlers::registerHandler);
            javalinServer.post("session", handlers::loginHandler);
            javalinServer.delete("session", handlers::logoutHandler);
            javalinServer.post("game", handlers::createGameHandler);
            javalinServer.put("game", handlers::joinGameHandler);
            javalinServer.get("game", handlers::listGamesHandler);
        } catch (Exception e) {
            FaultBarrierHandlers faultBarrier = new FaultBarrierHandlers();
            javalinServer.error(500, "Database failed to build.", faultBarrier::failureHandler);
        }
    }

    public int run(int desiredPort) {
        javalinServer.start(desiredPort);
        return javalinServer.port();
    }

    public void stop() {
        javalinServer.stop();
    }
}
