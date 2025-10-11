package server;

import chess.ChessGame;
import com.google.gson.Gson;
import exceptions.MissingFieldException;
import io.javalin.http.Context;

import java.util.Map;
import java.util.UUID;

public class Handlers {

    void register(Context ctx) throws MissingFieldException {
        var serializer = new Gson();
        String requestJson = ctx.body();
        var request = serializer.fromJson(requestJson, Map.class);
        // check for inputValidation
        try {
            areInputsValid(request);
        } catch (Exception e) {
            var response = Map.of("message", "Error: bad request");
            ctx.status(400);
            ctx.result(serializer.toJson(response));
            return;
        }

        // call to the service and register

        // this is targeted for specifically register, but this needs to be generalized for any kind of input. This is the HANDLER.
        var response = Map.of("username", request.get("username"), "authToken", UUID.randomUUID().toString());
        ctx.result(serializer.toJson(response));
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
