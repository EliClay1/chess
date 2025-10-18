package service;

import dataaccess.MemoryDataAccess;
import exceptions.InvalidException;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {

    @Test
    void createGame() throws Exception {
        String gameName = "game1";
        AuthData auth = new AuthData("bob", "1234567890");
        var db = new MemoryDataAccess();
        db.addAuth(auth);
        var gameService = new GameService(db);
        GameData newGame = gameService.createGame(gameName, auth.authToken());
        assertNotNull(newGame);
        assertEquals(gameName, newGame.gameName());
        assertInstanceOf(Integer.class, newGame.gameId());
    }

    @Test
    void noAuthDataCreateGame() {
        String gameName = "game1";
        AuthData auth = new AuthData("bob", null);
        var db = new MemoryDataAccess();
        var gameService = new GameService(db);
        assertThrows(InvalidException.class, () -> gameService.createGame(gameName, auth.authToken()));
    }

}