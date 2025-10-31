package service;

import dataaccess.MemoryDataAccess;
import exceptions.AlreadyTakenException;
import exceptions.DoesntExistException;
import exceptions.UnauthorizedException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// WRITTEN TO USE THE MEMORY DATABASE - BREAKS NOW.
class UserServiceTest {

    @Test
    void register() throws Exception {
        var user = new UserData("bob", "password", "b@gmail.com");
        var db = new MemoryDataAccess();
        var userService = new UserService(db);
        var authData = userService.register(user);
        // sanitize inputs
        assertNotNull(authData);
        assertEquals(user.username(), authData.username());
        assertFalse(authData.authToken().isEmpty());
    }

    @Test
    void userAlreadyExistsRegister() {
        var user1 = new UserData("bob", "password", "b@gmail.com");
        var user2 = new UserData("bob", "password", "b@gmail.com");
        var db = new MemoryDataAccess();
        var userService = new UserService(db);
        db.createUser(user1);
        assertThrows(AlreadyTakenException.class, () -> userService.register(user2));
    }

    @Test
    void login() throws Exception {
        var newUser = new UserData("bob", "password", "b@gmail.com");
        var returningUser = new UserData("bob", "password", null);
        var db = new MemoryDataAccess();
        var userService = new UserService(db);
        db.createUser(newUser);
        var authData = userService.login(returningUser);
        assertNotNull(authData);
        assertEquals(returningUser.username(), authData.username());
        assertFalse(authData.authToken().isEmpty());
    }

    @Test
    void userDoesntExistLogin() {
        var newUser = new UserData("bob", "password", "b@gmail.com");
        var db = new MemoryDataAccess();
        var userService = new UserService(db);
        assertThrows(DoesntExistException.class, () -> userService.login(newUser));
    }

    @Test
    void passwordIncorrectLogin() {
        var newUser = new UserData("bob", "password", "b@gmail.com");
        var returningUser = new UserData("bob", "password!", null);
        var db = new MemoryDataAccess();
        var userService = new UserService(db);
        db.createUser(newUser);
        assertThrows(UnauthorizedException.class, () -> userService.login(returningUser));
    }

    @Test
    void logout() throws Exception {
        var newAuth = new AuthData("Bob", "1234567890");
        var db = new MemoryDataAccess();
        var userService = new UserService(db);
        db.addAuth(newAuth);
        userService.logout(newAuth);
        assertNull(db.getAuth(newAuth.authToken()));
    }

    @Test
    void invalidAuthTokenLogout() {
        var newAuth = new AuthData("Bob", "1234567890");
        var db = new MemoryDataAccess();
        var userService = new UserService(db);
        assertThrows(UnauthorizedException.class, () -> userService.logout(newAuth));
    }
}