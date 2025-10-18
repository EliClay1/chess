package service;

import dataaccess.DataAccess;
import exceptions.InvalidException;
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
        String username = userByAuth.username();
        GameData newGame = new GameData();

        return null;
    }

}
