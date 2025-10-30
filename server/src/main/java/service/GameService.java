package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import exceptions.AlreadyTakenException;
import exceptions.InvalidException;
import exceptions.UnauthorizedException;
import model.AuthData;
import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public record GameService(DataAccess dataAccess) {

    public GameData createGame(String gameName, String authToken) throws Exception {
        AuthData userByAuth = dataAccess.getAuth(authToken);
        // no auth token
        if (userByAuth == null) {
            throw new UnauthorizedException();
        }
        int gameID;
        GameData newGame = new GameData(0, null, null, gameName, new ChessGame());
        gameID = dataAccess.createGame(newGame);
        // TODO - find a way to refactor this.
        return new GameData(gameID, null, null, gameName, new ChessGame());
    }

    public void joinGame(String authToken, int gameID, String playerColor) throws Exception {
        AuthData userByAuth = dataAccess.getAuth(authToken);
        // no auth token
        if (userByAuth == null) {
            throw new UnauthorizedException();
        }
        String username = userByAuth.username();
        GameData gameByID = dataAccess.getGame(gameID);
        if (gameByID == null) {
            throw new InvalidException();
        }
        GameData updatedGame;
        // white path
        if (playerColor.equals("WHITE") && gameByID.whiteUsername() == null) {
            updatedGame = new GameData(gameID, username, gameByID.blackUsername(), gameByID.gameName(), gameByID.game());
        } else if (playerColor.equals("BLACK") && gameByID.blackUsername() == null) {
            updatedGame = new GameData(gameID, gameByID.whiteUsername(), username, gameByID.gameName(), gameByID.game());
        } else {
            throw new AlreadyTakenException();
        }
        dataAccess.updateGame(updatedGame);
    }

    public ArrayList<Map<String, String>> listGames(String authToken) throws Exception {
        AuthData userByAuth = dataAccess.getAuth(authToken);
        if (userByAuth == null) {
            throw new UnauthorizedException();
        }
        var games = dataAccess.listGames();
        ArrayList<Map<String, String>> arrayOfGameData = new ArrayList<>();

        for (var game : games) {
            Map<String, String> mapOfGameData = new HashMap<>();
            mapOfGameData.put("gameID", String.format("%d", game.gameID()));
            mapOfGameData.put("whiteUsername", game.whiteUsername());
            mapOfGameData.put("blackUsername", game.blackUsername());
            mapOfGameData.put("gameName", game.gameName());
            arrayOfGameData.add(mapOfGameData);
        }
        return arrayOfGameData;
    }
}