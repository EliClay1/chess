package server;

import io.javalin.*;

public class Server {

    private final Javalin javalinServer;

    public Server() {
        Handlers handlers = new Handlers();
        javalinServer = Javalin.create(config -> config.staticFiles.add("web"));

        // TODO - Change all auth errors to be UnauthorizedException.

        javalinServer.delete("db", handlers::clearHandler);
        javalinServer.post("user", handlers::registerHandler);
        javalinServer.post("session", handlers::loginHandler);
        javalinServer.delete("session", handlers::logoutHandler);
        javalinServer.post("game", handlers::createGameHandler);
        javalinServer.put("game", handlers::joinGameHandler);
        javalinServer.get("game", handlers::listGamesHandler);
    }

    public int run(int desiredPort) {
        javalinServer.start(desiredPort);
        return javalinServer.port();
    }

    public void stop() {
        javalinServer.stop();
    }
}
