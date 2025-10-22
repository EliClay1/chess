package server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dataaccess.MemoryDataAccess;
import exceptions.*;
import io.javalin.http.Context;
import model.AuthData;
import model.GameData;
import model.UserData;
import service.DataAccessService;
import service.GameService;
import service.UserService;

import java.lang.reflect.Type;
import java.util.*;

public class Handlers {

    private final MemoryDataAccess dataAccess = new MemoryDataAccess();
    private final UserService userService = new UserService(dataAccess);
    private final GameService gameService = new GameService(dataAccess);
    private final DataAccessService dataAccessService = new DataAccessService(dataAccess);
    private final Gson serializer = new Gson();
    private final List<String> availablePieces = Arrays.asList("WHITE", "BLACK");


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
            if (e instanceof AlreadyTakenException) {
                ctx.status(403).result("{ \"message\": \"Error: already taken\" }");
            } else {
                ctx.status(500).result(String.format("{{ \"message\": \"Error: %s\" }}", e));
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
            ctx.status(400).result(serializer.toJson(response));
            return;
        }

        // call to the service and register
        try {
            AuthData response = userService.login(request);
            ctx.result(serializer.toJson(response));

        } catch (Exception e) {
            if (e instanceof DoesntExistException) {
                ctx.status(401).result("{ \"message\": \"Error: bad request\" }");
            } else if (e instanceof UnauthorizedException) {
                ctx.status(401).result("{ \"message\": \"Error: unauthorized\" }");
            } else {
                ctx.status(500).result(String.format("{{ \"message\": \"Error: %s\" }}", e));
            }
        }
    }

    void logoutHandler(Context ctx) {
        String requestHeader = ctx.header("authorization");
//        AuthData request = serializer.fromJson(requestJson, AuthData.class);
        AuthData request = new AuthData(null, requestHeader);

        // call to the service and register
        try {
            userService.logout(request);
            ctx.result("{ }");

        } catch (Exception e) {
            if (e instanceof UnauthorizedException) {
                ctx.status(401).result("{ \"message\": \"Error: unauthorized\" }");
            } else {
                ctx.status(500).result(String.format("{{ \"message\": \"Error: %s\" }}", e));
            }
        }
    }

    void createGameHandler(Context ctx) {
        String requestHeader = ctx.header("authorization");
        String requestJson = ctx.body();
         GameData request = serializer.fromJson(requestJson, GameData.class);

        try {
            if (request.gameName() == null || request.gameName().isEmpty()) {throw new MissingFieldException();}
        } catch (Exception e) {
            var response = Map.of("message", "Error: bad request");
            ctx.status(400).result(serializer.toJson(response));
            return;
        }
        try {
            GameData newGame = gameService.createGame(request.gameName(), requestHeader);
            ctx.status(200).result(serializer.toJson(Map.of("gameID", newGame.gameID())));
        } catch (Exception e) {
            if (e instanceof UnauthorizedException) {
                ctx.status(401).result("{ \"message\": \"Error: unauthorized\" }");
            } else {
                ctx.status(500).result(String.format("{{ \"message\": \"Error: %s\" }}", e));
            }
        }
    }

    void joinGameHandler(Context ctx) {
        String requestHeader = ctx.header("authorization");
        String requestJson = ctx.body();
        // gets access to the request data without having to create a new record object to map the data to.
        Type type = new TypeToken<Map<String, String>>() {}.getType();
        Map<String, String> request = serializer.fromJson(requestJson, type);

        int gameID = -1;
        String teamColor = request.get("playerColor");
        String idAsString = request.get("gameID");
        if (idAsString != null && !idAsString.isEmpty()) {
            try {
                gameID = Integer.parseInt(request.get("gameID"));
            } catch (NumberFormatException e) {
                throw new RuntimeException(e);
            }
        }


        // Check if the piece is a valid color
        try {
            if (teamColor == null || teamColor.isEmpty() || !availablePieces.contains(teamColor)) {throw new MissingFieldException();}
        } catch (Exception e) {
            var response = Map.of("message", "Error: bad request");
            ctx.status(400).result(serializer.toJson(response));
            return;
        }

        try {
            gameService.joinGame(requestHeader, gameID, teamColor);
            ctx.result("{ }");
        } catch (Exception e) {
            if (!(e instanceof UnauthorizedException)) {
                if (e instanceof InvalidException) {
                    ctx.status(400).result("{ \"message\": \"Error: bad request\" }");
                } else if (e instanceof AlreadyTakenException) {
                    ctx.status(403).result("{ \"message\": \"Error: already taken\" }");
                } else {
                    ctx.status(500).result(String.format("{{ \"message\": \"Error: %s\" }}", e));
                }
            } else {
                ctx.status(401).result("{ \"message\": \"Error: unauthorized\" }");
            }
        }

    }

    void listGamesHandler(Context ctx) {
        String requestHeader = ctx.header("authorization");
        try {
            Map<String, ArrayList<Map<String, String>>> resultMap = new HashMap<>();
            ArrayList<Map<String, String>> gameList = gameService.listGames(requestHeader);
            resultMap.put("games", gameList);
            ctx.status(200).result(serializer.toJson(resultMap));
        } catch (Exception e) {
            if (e instanceof UnauthorizedException) {
                ctx.status(401).result("{ \"message\": \"Error: unauthorized\" }");
            } else {
                ctx.status(500).result(String.format("{{ \"message\": \"Error: %s\" }}", e));
            }
        }
    }

    void clearHandler(Context ctx) {
        try {
            dataAccessService.clearAllData();
            ctx.status(200).result("{}");
        } catch (Exception e) {
            ctx.status(500).result(String.format("{{ \"message\": \"Error: %s\" }}", e));
        }
    }

}
