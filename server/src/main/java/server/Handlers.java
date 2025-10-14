package server;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.MemoryDataAccess;
import exceptions.AlreadyTakenException;
import exceptions.MissingFieldException;
import io.javalin.http.Context;
import model.AuthData;
import model.UserData;
import service.UserService;

import java.util.Map;

public class Handlers {

    UserService userService;

    void registerHandler(Context ctx) throws Exception {

        var dataAccess = new MemoryDataAccess();
        var userService = new UserService(dataAccess);

        var serializer = new Gson();
        String requestJson = ctx.body();
        UserData request = serializer.fromJson(requestJson, UserData.class);
        // check for inputValidation
        try {
            // TODO - FIX input Validation
//            areInputsValid();
            System.out.println("Input Validation");
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
            ctx.status(401).result(msg);
        }


    }

    public static <K, V> void areInputsValid(Map<K, V> inputMap) throws Exception {
        for (var key : inputMap.keySet()) {
            var value = inputMap.get(key);
            if (value == null) {
                throw new Exception();
            } else if (value instanceof String && ((String) value).isEmpty()) {
                throw new Exception();
            }
            // TODO: Check for sanitization, preventing SQL injection. Not required, but would be cool to add...
        }
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
