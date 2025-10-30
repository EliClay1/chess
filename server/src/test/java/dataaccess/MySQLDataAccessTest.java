package dataaccess;

import chess.ChessGame;
import exceptions.AlreadyTakenException;
import exceptions.DataAccessException;
import exceptions.DoesntExistException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
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


    @BeforeEach
    public void init() throws Exception {
        db = new MySQLDataAccess();
        userService = new UserService(db);
        gameService = new GameService(db);
        dataAccessService = new DataAccessService(db);

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
        userService.register(newUser);
        AuthData response = userService.login(newUser);
        gameService.createGame("Wowzah", response.authToken());

    }

    @Test
    void createGameNegative() {

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
    void clear() throws Exception {
        try {
            var db = new MySQLDataAccess();
            db.createUser(newUser);
            db.addAuth(newAuth);
            db.createGame(newGame);


        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

    }
}