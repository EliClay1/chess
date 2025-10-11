package server;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import io.javalin.http.Context;

import java.util.Map;
import java.util.UUID;

public class Handlers {

    void register(Context ctx) {
        var serializer = new Gson();
        String requestJson = ctx.body();
        var request = serializer.fromJson(requestJson, Map.class);
        // check for inputValidation
        areInputsValid(request);

        // call to the service and register

        // this is targeted for specifically register, but this needs to be generalized for any kind of input. This is the HANDLER.
        var response = Map.of("username", request.get("username"), "authToken", UUID.randomUUID().toString());
        ctx.result(serializer.toJson(response));
    }

    public static <K, V> void areInputsValid(Map<K, V> inputMap) {
        // currently this only checks if the username is "username", etc.
        for (var key : inputMap.keySet()) {
            var value = inputMap.get(key);
            if (value == null) {
                System.out.println("ERROR: YOU ARE MISSING SOME KIND OF VALUE!");
            } else if (value instanceof String && ((String) value).isEmpty()) {
                System.out.println("ERROR: YOU ARE MISSING SOME KIND OF VALUE!");
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
