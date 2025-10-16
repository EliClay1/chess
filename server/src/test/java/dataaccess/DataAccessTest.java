package dataaccess;

import model.UserData;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.Test;

import java.awt.image.MemoryImageSource;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class DataAccessTest {

    @Test
    void clear() {
        var dataBase = new MemoryDataAccess();
        dataBase.createUser(new UserData("bob", "password", "e@gmail.com"));
        dataBase.clear();
        assertNull(dataBase.getUserByName("bob"));
    }

    @Test
    void createUser() {
        var dataBase = new MemoryDataAccess();
        var user = new UserData("bob", "password", "e@gmail.com");
        dataBase.createUser(user);
        assertEquals(user, dataBase.getUserByName(user.username()));
    }

    @Test
    void getUserByName() {
    }
}