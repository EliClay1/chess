package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;

public class MemoryDataAccess implements DataAccess{

    private final HashMap<String, UserData> usersByName = new HashMap<>();
    private final HashMap<String, AuthData> auth = new HashMap<>();

    @Override
    public void clear() {
        usersByName.clear();
    }

    @Override
    public void createUser(UserData user) {
        usersByName.put(user.username(), user);
    }

    @Override
    public UserData getUserByName(String username) {
        return usersByName.get(username);
    }

    @Override
    public void addAuth(AuthData authData) {
        auth.put(authData.authToken(), authData);
    }

    @Override
    public AuthData getUserByAuth(String authToken) {
        return auth.get(authToken);
    }


}
