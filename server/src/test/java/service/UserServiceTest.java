package service;

import dataaccess.MemoryDataAccess;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Test
    void register() throws Exception {
        var user = new UserData("bob", "password", "b@gmail.com");
        var db = new MemoryDataAccess();
        var userService = new UserService(db);
        var authData = userService.register(user);
        assertNotNull(authData);
        assertEquals(user.username(), authData.username());
//        assertNotNull(authData.authToken()); below is a better version of this.
        assertTrue(authData.authToken().isEmpty());
    }
}