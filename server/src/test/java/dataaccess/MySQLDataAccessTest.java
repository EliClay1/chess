package dataaccess;

import chess.ChessGame;
import exceptions.DataAccessException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MySQLDataAccessTest {

    private static UserData newUser;
    private static UserData existingUser;
    private static AuthData newAuth;
    private static GameData newGame;

    @BeforeAll
    public static void init() {
        newUser = new UserData("Bob", "s3cur3Passw0rd", "bob@gmail.com");
        existingUser = new UserData("Jerry", "123456790", "jerry.king@gmail.com");
        newAuth = new AuthData("Bob", "01923854972987342");
        newGame = new GameData(1, null, null, "game1", new ChessGame());
    }

    @Test
    void createUser() {
        try {
            var db = new MySQLDataAccess();
            db.createUser(newUser);

        } catch (Exception e) {
            // empty for now. Not sure what to put here.
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