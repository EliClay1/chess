package service;

import chess.ChessGame;
import dataaccess.DataAccess;
import exceptions.AlreadyTakenException;
import exceptions.InvalidException;
import exceptions.UnauthorizedException;
import model.AuthData;
import model.GameData;

public class GameService {
    private final DataAccess dataAccess;

    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public GameData createGame(String gameName, String authToken) throws Exception {
        AuthData userByAuth = dataAccess.getAuth(authToken);
        // no auth token
        if (userByAuth == null) {
            throw new InvalidException();
        }
        GameData newGame = new GameData(dataAccess.createID(), null, null, gameName, new ChessGame());
        dataAccess.createGame(newGame);
        return newGame;
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
}