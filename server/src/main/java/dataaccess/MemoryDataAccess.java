package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryDataAccess implements DataAccess{

    private final HashMap<String, UserData> usersByName = new HashMap<>();

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
}
