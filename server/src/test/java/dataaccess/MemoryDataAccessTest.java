package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MemoryDataAccessTest {

    @Test
    void clear() {
        var dataBase = new MemoryDataAccess();
        dataBase.createUser(new UserData("bob", "password", "e@gmail.com"));
        dataBase.clear();
        assertNull(dataBase.getUser("bob"));
    }

    @Test
    void createUser() {
        var dataBase = new MemoryDataAccess();
        var user = new UserData("bob", "password", "e@gmail.com");
        dataBase.createUser(user);
        assertEquals(user, dataBase.getUser(user.username()));
    }

    @Test
    void getUserByName() {
    }

    @Test
    void addAuth() {
        var db = new MemoryDataAccess();
        var auth = new AuthData("bob", "123");
        db.addAuth(auth);
        assertEquals(auth, db.getAuth(auth.authToken()));
    }

    @Test
    void getUserByAuth() {
        var db = new MemoryDataAccess();
        var auth = new AuthData("bob", "123");
        db.addAuth(auth);
        assertEquals(auth.username(), db.getAuth(auth.authToken()).username());
    }
}