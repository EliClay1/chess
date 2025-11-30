package service;

import chess.*;
import com.google.gson.Gson;
import com.google.gson.JsonSerializer;
import dataaccess.DataAccess;
import exceptions.AlreadyTakenException;
import exceptions.InvalidException;
import exceptions.UnauthorizedException;
import model.AuthData;
import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    // TODO - catch a request times out so that teams can't be joined and then locked out.
    public void joinGame(String authToken, int gameId, String playerColor) throws Exception {
        AuthData userByAuth = dataAccess.getAuth(authToken);
        // no auth token
        if (userByAuth == null) {
            throw new UnauthorizedException();
        }
        String username = userByAuth.username();
        GameData gameByID = dataAccess.getGame(gameId);
        if (gameByID == null) {
            throw new InvalidException();
        }
        GameData updatedGame;
        // white path
        if (playerColor.equalsIgnoreCase("white") && gameByID.whiteUsername() == null) {
            updatedGame = new GameData(gameId, username, gameByID.blackUsername(), gameByID.gameName(), gameByID.game());
        } else if (playerColor.equalsIgnoreCase("black") && gameByID.blackUsername() == null) {
            updatedGame = new GameData(gameId, gameByID.whiteUsername(), username, gameByID.gameName(), gameByID.game());
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
            mapOfGameData.put("gameData", serializeFromGameObject(game.game()));
            arrayOfGameData.add(mapOfGameData);
        }
        return arrayOfGameData;
    }

    public void makeMove(String authToken, List<String> chessMove, int gameId, ChessPiece.PieceType promo) throws Exception {
        AuthData userByAuth = dataAccess.getAuth(authToken);
        if (userByAuth == null) {
            throw new UnauthorizedException();
        }

        GameData gameData = dataAccess.getGame(gameId);
        if (gameData.whiteUsername() == null || gameData.blackUsername() == null) {
            // TODO - make it return an error of some kind, not neough players to start the game, something.
            throw new NotUsersTurnException("Not enough players to begin the game!");
        }


        // parses the information into positional moves.
        List<ChessPosition> positions = new ArrayList<>(List.of());

        for (var position : chessMove) {
            String input = position.trim().toLowerCase();
            char yChar = input.charAt(0);
            char xChar = input.charAt(1);
            int yInt = (yChar - 'a') + 1;
            int xInt = (xChar - '1') + 1;
            positions.add(new ChessPosition(xInt, yInt));
        }

        // TODO - Send an error if it isn't the user's turn.
        ChessMove move;
        if (promo == null) {
            move = new ChessMove(positions.getFirst(), positions.getLast(), null);
        } else {
            move = new ChessMove(positions.getFirst(), positions.getLast(), promo);
        }

        ChessGame game = gameData.game();
        game.makeMove(move);
        dataAccess.updateGame(new GameData(gameData.gameID(), gameData.whiteUsername(),
                gameData.blackUsername(), gameData.gameName(), game));
    }

    private String serializeFromGameObject(ChessGame chessGame) {
        return new Gson().toJson(chessGame);
    }
}