package service;

import dataaccess.MemoryDataAccess;
import exceptions.AlreadyTakenException;
import exceptions.InvalidException;
import exceptions.UnauthorizedException;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

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
        assertInstanceOf(Integer.class, newGame.gameID());
    }

    @Test
    void noAuthDataCreateGame() {
        String gameName = "game1";
        AuthData auth = new AuthData("bob", null);
        var db = new MemoryDataAccess();
        var gameService = new GameService(db);
        assertThrows(UnauthorizedException.class, () -> gameService.createGame(gameName, auth.authToken()));
    }

    @Test
    void joinGame() throws Exception {
        AuthData auth = new AuthData("bob", "1234567890");
        var db = new MemoryDataAccess();
        db.addAuth(auth);
        var gameService = new GameService(db);
        GameData newGame = gameService.createGame("game1", auth.authToken());
        gameService.joinGame(auth.authToken(), newGame.gameID(), "WHITE");
        GameData updatedGame = db.getGame(newGame.gameID());
        assertNotNull(updatedGame);
        if (updatedGame.whiteUsername() == null) {
            assertNotNull(updatedGame.blackUsername());
        } else if (updatedGame.blackUsername() == null) {
            assertNotNull(updatedGame.whiteUsername());
        }
        // TODO - not sure what else needs to be asserted here.
    }

    @Test
    void noAuthDataJoinGame() throws Exception {
        AuthData auth = new AuthData("bob", "1234567890");
        AuthData joiningAuth = new AuthData("jerry", "0987654321");
        var db = new MemoryDataAccess();
        db.addAuth(auth);
        var gameService = new GameService(db);
        GameData newGame = gameService.createGame("game1", auth.authToken());
        assertThrows(UnauthorizedException.class, () -> gameService.joinGame(joiningAuth.authToken(), newGame.gameID(), "WHITE"));
    }

    @Test
    void invalidGameIDJoinGame() throws Exception {
        AuthData auth = new AuthData("bob", "1234567890");
        AuthData joiningAuth = new AuthData("jerry", "0987654321");
        var db = new MemoryDataAccess();
        db.addAuth(auth);
        db.addAuth(joiningAuth);
        var gameService = new GameService(db);
        assertThrows(InvalidException.class, () -> gameService.joinGame(joiningAuth.authToken(), 10, "WHITE"));
    }

    @Test
    void whiteAlreadyTakenJoinGame() throws Exception {
        AuthData auth = new AuthData("bob", "1234567890");
        AuthData joiningAuth = new AuthData("jerry", "0987654321");
        var db = new MemoryDataAccess();
        db.addAuth(auth);
        db.addAuth(joiningAuth);
        var gameService = new GameService(db);
        GameData newGame = gameService.createGame("game1", auth.authToken());
        gameService.joinGame(auth.authToken(), newGame.gameID(), "WHITE");
        assertThrows(AlreadyTakenException.class, () -> gameService.joinGame(joiningAuth.authToken(), newGame.gameID(), "WHITE"));
    }

    @Test
    void blackAlreadyTakenJoinGame() throws Exception {
        AuthData auth = new AuthData("bob", "1234567890");
        AuthData joiningAuth = new AuthData("jerry", "0987654321");
        var db = new MemoryDataAccess();
        db.addAuth(auth);
        db.addAuth(joiningAuth);
        var gameService = new GameService(db);
        GameData newGame = gameService.createGame("game1", auth.authToken());
        gameService.joinGame(auth.authToken(), newGame.gameID(), "BLACK");
        assertThrows(AlreadyTakenException.class, () -> gameService.joinGame(joiningAuth.authToken(), newGame.gameID(), "BLACK"));
    }

    @Test
    void getGames() throws Exception {
        AuthData auth = new AuthData("bob", "1234567890");
        var db = new MemoryDataAccess();
        db.addAuth(auth);
        var gameService = new GameService(db);
        // loop to create 10 games
        for (int i = 0; i < 10; i++) {
            gameService.createGame(String.format("game%d", i), auth.authToken());
        }
        var listOfGames = gameService.listGames(auth.authToken());

        assertNotNull(listOfGames);
        assertInstanceOf(ArrayList.class, listOfGames);
        assertNotNull(listOfGames.getFirst().get("gameID"));
        assertNotNull(listOfGames.getLast().get("gameID"));
        // TODO - what other tests need to go here?
    }

    @Test
    void noAuthDataListGames() throws Exception {
        AuthData auth = new AuthData("bob", "1234567890");
        var db = new MemoryDataAccess();
        db.addAuth(auth);
        var gameService = new GameService(db);
        // loop to create 10 games
        for (int i = 0; i < 10; i++) {
            gameService.createGame(String.format("game%d", i), auth.authToken());
        }
        db.deleteAuth(auth);
        assertThrows(UnauthorizedException.class, () -> gameService.listGames(auth.authToken()));
    }

}