package server;

import com.google.gson.Gson;
import dataaccess.MemoryDataAccess;
import exceptions.AlreadyTakenException;
import exceptions.MissingFieldException;
import io.javalin.http.Context;
import model.AuthData;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import service.UserService;

import java.util.Map;

public class Handlers {

    private final MemoryDataAccess dataAccess = new MemoryDataAccess();
    private final UserService userService = new UserService(dataAccess);
    private final Gson serializer = new Gson();


    void registerHandler(Context ctx) {
        String requestJson = ctx.body();
        UserData request = serializer.fromJson(requestJson, UserData.class);
        // checks for input validation
        try {
            if (request.username() == null || request.username().isEmpty()) {throw new MissingFieldException();}
            if (request.email() == null || request.email().isEmpty()) {throw new MissingFieldException();}
            if (request.password() == null || request.password().isEmpty()) {throw new MissingFieldException();}
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
            if (e.equals(new AlreadyTakenException())) {
                ctx.status(403).result("{ \"message\": \"Error: already taken\" }");
            }
        }
    }

    void loginHandler(Context ctx) {
        String requestJson = ctx.body();
        UserData request = serializer.fromJson(requestJson, UserData.class);
        // checks for input validation
        try {
            if (request.username() == null || request.username().isEmpty()) {throw new MissingFieldException();}
            if (request.password() == null || request.password().isEmpty()) {throw new MissingFieldException();}
        } catch (Exception e) {
            var response = Map.of("message", "Error: bad request");
            ctx.status(400);
            ctx.result(serializer.toJson(response));
            return;
        }

        // call to the service and register
        try {
            AuthData response = userService.login(request);
            ctx.result(serializer.toJson(response));

        } catch (Exception e) {
            if (e.equals(new AlreadyTakenException())) {
                ctx.status(403).result("{ \"message\": \"Error: already taken\" }");
            }
        }
    }

    void clearHandler(Context ctx) {
        dataAccess.clear();
        ctx.status(200).result("{}");

    }


}

//    // code copied from spec
//    void serializeGame() {
//        var serializer = new Gson();
//
//        var game = new ChessGame();
//
//        var json = serializer.toJson(game);
//
//        game = serializer.fromJson(json, ChessGame.class);
//    }
//}
