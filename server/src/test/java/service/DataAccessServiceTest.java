package service;

import dataaccess.FailingDataAccess;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataAccessServiceTest {

    @Test
    void clearAllData() throws Exception {
        AuthData auth = new AuthData("test_admin", "1234567890");
        var db = new MemoryDataAccess();
        db.addAuth(auth);
        var gameService = new GameService(db);
        var userService = new UserService(db);
        String[] gameNames = {"g1", "g2", "g3", "g4", "g5"};
        String[] usernames = {"user1", "user2", "user3", "user4", "user5"};
        for (String name : gameNames) {
            gameService.createGame(name, auth.authToken());
        }
        for (String name : usernames) {
            userService.register(new UserData(name, "Password!", String.format("%s@gmail.com", name)));
        }
        var dataService = new DataAccessService(db);
        dataService.clearAllData();
        assertTrue(true);
    }

    @Test
    void clearDataError() throws Exception {
        AuthData auth = new AuthData("test_admin", "1234567890");
        var db = new MemoryDataAccess();
        db.addAuth(auth);
        var gameService = new GameService(db);
        var userService = new UserService(db);
        String[] gameNames = {"g1", "g2", "g3", "g4", "g5"};
        String[] usernames = {"user1", "user2", "user3", "user4", "user5"};
        for (String name : gameNames) {
            gameService.createGame(name, auth.authToken());
        }
        for (String name : usernames) {
            userService.register(new UserData(name, "Password!", String.format("%s@gmail.com", name)));
        }
        var dataService = new DataAccessService(new FailingDataAccess());
        assertThrows(Exception.class, dataService::clearAllData);
    }
}