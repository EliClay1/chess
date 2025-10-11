package server;

import com.google.gson.Gson;
import io.javalin.http.Context;

import java.util.Map;
import java.util.UUID;

public class Handlers {

    void register(Context ctx) {
        var serializer = new Gson();
        String requestJson = ctx.body();

        var request = serializer.fromJson(requestJson, Map.class);

        // call to the service and register

        // this is targeted for specifically register, but this needs to be generalized for any kind of input. This is the HANDLER.
        var response = Map.of("username", request.get("username"), "authToken", UUID.randomUUID().toString());
        ctx.result(serializer.toJson(response));
    }
}
