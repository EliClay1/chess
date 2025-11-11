package server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dataaccess.MySQLDataAccess;
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

    private final MySQLDataAccess db;
    private final UserService userService;
    private final DataAccessService dataAccessService;
    private final GameService gameService;
    private final Gson serializer = new Gson();
    private final List<String> availablePieces = Arrays.asList("white", "black");

    // This is private so that the database can be safely initialized.
    private Handlers(MySQLDataAccess dataAccess) {
        this.db = dataAccess;
        this.userService = new UserService(db);
        this.gameService = new GameService(db);
        this.dataAccessService= new DataAccessService(db);
    }

    public static Handlers createHandlersWithDatabase() throws DataAccessException {
        MySQLDataAccess dataAccess = attemptDatabaseCreation();
        return new Handlers(dataAccess);
    }

    private static MySQLDataAccess attemptDatabaseCreation() throws DataAccessException {
        try {
            return new MySQLDataAccess();
        } catch (Exception e) {
            throw new DataAccessException(String.format("Database initalization error: %s", e.getMessage()));
        }
    }

    void registerHandler(Context ctx) {
        String requestJson = ctx.body();
        // TODO - error raised when "" is inputted as pasword. Might need to run some error handling here.
        UserData request = serializer.fromJson(requestJson, UserData.class);

        // checks for input validation
        try {
            if (request.username() == null || request.username().isEmpty()) {throw new MissingFieldException();}
            if (request.email() == null || request.email().isEmpty()) {throw new MissingFieldException();}
            if (request.password() == null || request.password().isEmpty()) {throw new MissingFieldException();}
        } catch (MissingFieldException e) {
            errorReturnHandling(ctx, e);
            return;
        }

        // call to the service and register
        try {
            AuthData response = userService.register(request);
            ctx.result(serializer.toJson(response));
        } catch (Exception e) {
            errorReturnHandling(ctx, e);
        }
    }

    void loginHandler(Context ctx) {
        String requestJson = ctx.body();
        UserData request = serializer.fromJson(requestJson, UserData.class);
        // checks for input validation
        try {
            if (request.username() == null || request.username().isEmpty()) {throw new MissingFieldException();}
            if (request.password() == null || request.password().isEmpty()) {throw new MissingFieldException();}
        } catch (MissingFieldException e) {
            errorReturnHandling(ctx, e);
            return;
        }

        // call to the service and register
        try {
            AuthData response = userService.login(request);
            ctx.result(serializer.toJson(response));

        } catch (Exception e) {
            errorReturnHandling(ctx, e);
        }
    }

    void logoutHandler(Context ctx) {
        String requestHeader = ctx.header("authorization");
//        AuthData request = serializer.fromJson(requestJson, AuthData.class);
        AuthData request = new AuthData(null, requestHeader);

        // call to the service and register
        try {
            userService.logout(request);
            ctx.result("{}");

        } catch (Exception e) {
            errorReturnHandling(ctx, e);
        }
    }

    void createGameHandler(Context ctx) {
        String requestHeader = ctx.header("authorization");
        String requestJson = ctx.body();
         GameData request = serializer.fromJson(requestJson, GameData.class);

        try {
            if (request.gameName() == null || request.gameName().isEmpty()) {throw new MissingFieldException();}
        } catch (MissingFieldException e) {
            errorReturnHandling(ctx, e);
            return;
        }
        try {
            GameData newGame = gameService.createGame(request.gameName(), requestHeader);
            ctx.status(200).result(serializer.toJson(Map.of("gameID", newGame.gameID())));
        } catch (Exception e) {
            errorReturnHandling(ctx, e);
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
                errorReturnHandling(ctx, e);
            }
        }


        // Check if the piece is a valid color
        try {
            if (teamColor == null || teamColor.isEmpty() || !availablePieces.contains(teamColor.toLowerCase())) {throw new MissingFieldException();}
        } catch (MissingFieldException e) {
            errorReturnHandling(ctx, e);
            return;
        }

        try {
            gameService.joinGame(requestHeader, gameID, teamColor);
            ctx.result("{}");
        } catch (Exception e) {
            errorReturnHandling(ctx, e);
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
            errorReturnHandling(ctx, e);
        }
    }

    void clearHandler(Context ctx) {
        try {
            dataAccessService.clearAllData();
            ctx.status(200).result("{}");
        } catch (Exception e) {
            var response = Map.of("message", String.format("Error: %s", e.getMessage()));
            ctx.status(500).result(serializer.toJson(response));
        }
    }

    private void errorReturnHandling(Context ctx, Exception e) {
        var response = Map.of();
        // FIXME - rearrage errors, ensure that there is an error for each problem, don't mix them together.

        // FIXME - Ensure that error handling is happening at the root of the problem, and arrange that data / information accordingly. Follow OWASP #9

        //noinspection IfCanBeSwitch
        if (e instanceof DoesntExistException) {
            response = Map.of("message", String.format("Error: bad request, %s", e.getMessage()));
            ctx.status(401).result(serializer.toJson(response));

        } else if (e instanceof UnauthorizedException) {
            response = Map.of("message", String.format("Error: unauthorized, %s", e.getMessage()));
            ctx.status(401).result(serializer.toJson(response));

        } else if (e instanceof MissingFieldException) {
            response = Map.of("message", String.format("Error: bad request, %s", e.getMessage()));
            ctx.status(400).result(serializer.toJson(response));

        } else if (e instanceof AlreadyTakenException) {
            response = Map.of("message", String.format("Error: already taken, %s", e.getMessage()));
            ctx.status(403).result(serializer.toJson(response));

        } else if (e instanceof InvalidException) {
            response = Map.of("message", String.format("Error: bad request, %s", e.getMessage()));
            ctx.status(400).result(serializer.toJson(response));

        } else if (e instanceof NumberFormatException) {
            response = Map.of("message", String.format("Error: Found string where there should be int, %s", e.getMessage()));
            ctx.status(400).result(serializer.toJson(response));
        }
        else {
            response = Map.of("message", String.format("Error: %s", e.getMessage()));
            ctx.status(500).result(serializer.toJson(response));
        }
    }
}
