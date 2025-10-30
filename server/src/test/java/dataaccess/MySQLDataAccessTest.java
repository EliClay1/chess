package dataaccess;

import chess.ChessGame;
import exceptions.AlreadyTakenException;
import exceptions.DoesntExistException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import service.GameService;
import service.UserService;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class MySQLDataAccessTest {

    private static MySQLDataAccess db;
    private static UserData newUser;
    private static UserData existingUser;
    private static AuthData newAuth;
    private static GameData newGame;
    private static UserService userService;
    private static GameService gameService;


    @BeforeEach
    public void init() throws Exception {
        db = new MySQLDataAccess();
        userService = new UserService(db);
        gameService = new GameService(db);

        newUser = new UserData("Bob", "s3cur3Passw0rd", "bob@gmail.com");
        existingUser = new UserData("Bob", "123456790", "bob.king@gmail.com");
        newAuth = new AuthData("Bob", "01923854972987342");
        newGame = new GameData(1, null, null, "game1", new ChessGame());
    }

    @AfterEach
    public void deInit() throws Exception {
        db.clear();
    }

    @Test
    void createUser() throws Exception {
        db.clear();
        userService.register(newUser);
        UserData returnedUser = db.getUser(newUser.username());
        assertEquals(returnedUser.username(), newUser.username());
    }

    @Test
    void createUserNegative() throws Exception {
        var db = new MySQLDataAccess();
        db.createUser(existingUser);
        assertThrows(AlreadyTakenException.class, () -> userService.register(newUser));
    }

    @Test
    void getUser() throws Exception {
        userService.register(newUser);
        assertNotNull(db.getUser(newUser.username()));
    }

    @Test
    void getUserNegative() throws Exception {
        assertNull(db.getUser(newUser.username()));
    }

    @Test
    void addAuth() throws Exception {
        db.addAuth(newAuth);
        AuthData returnedAuth = db.getAuth(newAuth.authToken());
        assertEquals(newAuth.authToken(), returnedAuth.authToken());
    }

    @Test
        void addAuthNegative() throws Exception {
        db.addAuth(newAuth);
        assertThrows(AlreadyTakenException.class, () -> db.addAuth(newAuth));
    }

    @Test
    void getAuth() throws Exception {
        db.addAuth(newAuth);
        assertNotNull(db.getAuth(newAuth.authToken()));
    }

    @Test
    void getAuthNegative() throws Exception {
        assertNull(db.getAuth("0"));
    }

    @Test
    void deleteAuth() throws Exception {
        db.addAuth(newAuth);
        db.deleteAuth(newAuth);
        assertNull(db.getAuth(newAuth.authToken()));
    }

    @Test
    void deleteAuthNegative() {
        assertThrows(DoesntExistException.class, () -> db.deleteAuth(new AuthData("Kerry", "0")));
    }

    @Test
    void createGame() throws Exception {
        int gameID = db.createGame(new GameData(0, null, null, "wowzah", new ChessGame()));
        assertInstanceOf(GameData.class, db.getGame(gameID));
    }

    @Test
    void createGameNegative() throws Exception {
        int gameID = db.createGame(new GameData(0, null, null, "wowzah", null));
        assertNull(db.getGame(gameID).game());
    }

    @Test
    void getGame() throws Exception {
        int gameID = db.createGame(new GameData(0, null, null, "wowzah", new ChessGame()));
        GameData returnedGame = db.getGame(gameID);
        assertInstanceOf(GameData.class, returnedGame);
    }

    @Test
    void getGameNegative() throws Exception {
        db.createGame(new GameData(0, null, null, "wowzah", new ChessGame()));
        assertNull(db.getGame(0));
    }

    @Test
    void listGames() throws Exception {
        db.addAuth(newAuth);
        // loop to create 10 games
        for (int i = 0; i < 10; i++) {
            gameService.createGame(String.format("game%d", i), newAuth.authToken());
        }
        var listOfGames = gameService.listGames(newAuth.authToken());
        assertNotNull(listOfGames);
        assertInstanceOf(ArrayList.class, listOfGames);
        assertNotNull(listOfGames.getFirst().get("gameID"));
        assertNotNull(listOfGames.getLast().get("gameID"));
    }

    @Test
    void listGamesNegative() throws Exception {
        var listOfGames = db.listGames();
        assertTrue(listOfGames == null || listOfGames.isEmpty());
    }

    @Test
    void updateGame() throws Exception {
        GameData updatedGame = new GameData(newGame.gameID(), "Bob", null, newGame.gameName(), newGame.game());
        db.updateGame(updatedGame);
        assertEquals(updatedGame, db.getGame(updatedGame.gameID()));
    }

    @Test
    void updateGameNegative() {
        assertThrows(Exception.class, () -> db.updateGame(null));
    }

    @Test
    void clear() throws Exception {
        var db = new MySQLDataAccess();
        db.createUser(newUser);
        db.addAuth(newAuth);
        db.createGame(newGame);
    }
}