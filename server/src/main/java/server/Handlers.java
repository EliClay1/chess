package server;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.MemoryDataAccess;
import exceptions.AlreadyTakenException;
import io.javalin.http.Context;
import model.AuthData;
import model.UserData;
import service.UserService;

import java.util.Map;

public class Handlers {

    UserService userService;
    private final MemoryDataAccess dataAccess = new MemoryDataAccess();

    void registerHandler(Context ctx) throws Exception {
        var userService = new UserService(dataAccess);

        var serializer = new Gson();
        String requestJson = ctx.body();
        UserData request = serializer.fromJson(requestJson, UserData.class);
        // checks for input validation
        try {
            if (request.username() == null || request.username().isEmpty()) {throw new AlreadyTakenException("Empty username.");}
            if (request.email() == null || request.email().isEmpty()) {throw new AlreadyTakenException("Empty email.");}
            if (request.password() == null || request.password().isEmpty()) {throw new AlreadyTakenException("Empty password.");}
        } catch (Exception e) {
            var response = Map.of("message", "Error: bad request");
            ctx.status(400);
            ctx.result(serializer.toJson(response));
            return;
        }

        // call to the service and register
        try {
            AuthData response = userService.register(request);
            ctx.result(serializer.toJson(response));
        } catch (Exception e) {
            var msg = String.format("{ \"message\": \"Error: already taken\" }", e.getMessage());
            ctx.status(403).result(msg);
        }
    }

    void clearHandler(Context ctx) {
        dataAccess.clear();
        ctx.status(200).result("{}");

    }

    // code copied from spec
    void serializeGame() {
        var serializer = new Gson();

        var game = new ChessGame();

// serialize to JSON
        var json = serializer.toJson(game);

// deserialize back to ChessGame
        game = serializer.fromJson(json, ChessGame.class);
    }
}
