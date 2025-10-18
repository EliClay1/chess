package server;

import io.javalin.*;

public class Server {

    private final Javalin javalinServer;


    public Server() {
        Handlers handlers = new Handlers();
        javalinServer = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.

        javalinServer.delete("db", handlers::clearHandler);
        javalinServer.post("user", handlers::registerHandler);
        javalinServer.post("session", handlers::loginHandler);
        javalinServer.delete("session", handlers::logoutHandler);
        // TODO - Create Game
        javalinServer.post("game", handlers::createGameHandler);

        // TODO - List Games



        // TODO - Join Game

        // TODO - Clear Application

    }

    public int run(int desiredPort) {
        javalinServer.start(desiredPort);
        return javalinServer.port();
    }

    public void stop() {
        javalinServer.stop();
    }
}
