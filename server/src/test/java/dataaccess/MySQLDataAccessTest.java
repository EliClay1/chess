package dataaccess;

import chess.ChessGame;
import exceptions.AlreadyTakenException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.DataAccessService;
import service.GameService;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

class MySQLDataAccessTest {

    private static MySQLDataAccess db;
    private static UserData newUser;
    private static UserData existingUser;
    private static AuthData newAuth;
    private static GameData newGame;
    private static UserService userService;
    private static GameService gameService;
    private static DataAccessService dataAccessService;


    @BeforeAll
    public static void init() throws Exception {
        db = new MySQLDataAccess();
        userService = new UserService(db);
        gameService = new GameService(db);
        dataAccessService = new DataAccessService(db);

        newUser = new UserData("Bob", "s3cur3Passw0rd", "bob@gmail.com");
        existingUser = new UserData("Bob", "123456790", "bob.king@gmail.com");
        newAuth = new AuthData("Bob", "01923854972987342");
        newGame = new GameData(1, null, null, "game1", new ChessGame());
    }

    @AfterAll
    public static void deInit() throws Exception {
        db.clear();
    }

    @Test
    void createUser() throws Exception {
        try {
            userService.register(newUser);
            UserData returnedUser = db.getUser(newUser.username());
            assertEquals(returnedUser.username(), newUser.username());
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Test
    void userAlreadyExists() throws Exception {
        try {
            var db = new MySQLDataAccess();
            db.createUser(existingUser);
            assertThrows(AlreadyTakenException.class, () -> userService.register(newUser));
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Test
    void getUser() {
    }

    @Test
    void addAuth() {
    }

    @Test
    void getAuth() {
    }

    @Test
    void deleteAuth() {
    }

    @Test
    void createGame() {
    }

    @Test
    void getGame() {
    }

    @Test
    void listGames() {
    }

    @Test
    void updateGame() {
    }

    @Test
    void createID() {
    }

    @Test
    void isEmpty() {
    }

    @Test
    void clear() {
        try {
            var db = new MySQLDataAccess();
            db.createUser(newUser);
            db.addAuth(newAuth);
            db.createGame(newGame);


        } catch (Exception e) {
            // empty for now. Not sure what to put here.
        }

    }
}