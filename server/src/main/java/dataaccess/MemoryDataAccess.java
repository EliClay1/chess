package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.HashMap;

public class MemoryDataAccess implements DataAccess{

    private final HashMap<String, UserData> usersByName = new HashMap<>();
//    private final HashMap<String, UserData> usersByAuth = new HashMap<>();
//    private final HashMap<String, AuthData> auths = new HashMap<>();
//    private final HashMap<String, GameData> games = new HashMap<>();

    @Override
    public void clear() {
        usersByName.clear();
    }

    @Override
    public void createUser(UserData user) {
        usersByName.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) {
        return usersByName.get(username);
    }

//    @Override
//    public UserData getUser(String authToken) {
//        return usersByName.get(authToken);
//    }
}
